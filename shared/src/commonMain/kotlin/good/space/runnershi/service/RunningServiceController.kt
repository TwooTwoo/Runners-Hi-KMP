package good.space.runnershi.service

import good.space.runnershi.model.domain.location.LocationModel
import good.space.runnershi.model.domain.location.LocationTracker
import good.space.runnershi.model.domain.running.MovementAnalyzer
import good.space.runnershi.model.domain.running.MovementStatus
import good.space.runnershi.repository.LocalRunningDataSource
import good.space.runnershi.settings.SettingsRepository
import good.space.runnershi.state.PauseType
import good.space.runnershi.state.RunningStateManager
import good.space.runnershi.util.DistanceCalculator
import good.space.runnershi.util.NotificationHelper
import good.space.runnershi.util.TimeFormatter
import good.space.runnershi.util.format
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class RunningServiceController(
    private val locationTracker: LocationTracker,
    private val runningDataSource: LocalRunningDataSource,
    private val settingsRepository: SettingsRepository,
    private val notificationHelper: NotificationHelper
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    @OptIn(ExperimentalTime::class)
    private val movementAnalyzer = MovementAnalyzer()

    private var timerJob: Job? = null
    private var trackingJob: Job? = null
    private var lastLocation: LocationModel? = null

    /**
     * 러닝 시작
     */
    @OptIn(ExperimentalTime::class)
    fun startRunning() {
        RunningStateManager.reset()
        RunningStateManager.setStartTime(Clock.System.now())
        RunningStateManager.setRunningState(true)
        RunningStateManager.addEmptySegment()

        // 분석기 초기화
        movementAnalyzer.start(initialStatus = MovementStatus.MOVING)

        scope.launch {
            runningDataSource.startRun()
        }

        notificationHelper.startRunningNotification("00:00", "0.00 km")
        startTimer()
        startLocationTracking()
    }

    /**
     * [CASE 1 & 2] 러닝 재개
     * - 사용자가 '재개' 버튼 클릭 (USER_PAUSE 상태에서)
     * - 과속 모달에서 '이어달리기' 클릭 (AUTO_PAUSE_VEHICLE 상태에서)
     */
    fun resumeRunning() {
        // 상태 초기화 및 세그먼트 분리
        RunningStateManager.resume()
        RunningStateManager.addEmptySegment()

        scope.launch {
            runningDataSource.incrementSegmentIndex()
        }

        lastLocation = null // 위치 튐 방지

        // [중요] 재개 직후에는 '움직임' 상태로 강제 설정하여 즉시 반응하도록 함
        movementAnalyzer.start(initialStatus = MovementStatus.MOVING)

        updateNotificationUI()
        startTimer()
        startLocationTracking()
    }

    /**
     * [CASE 1] 사용자 직접 일시정지
     * - UI의 일시정지 버튼이 '재개' 버튼으로 바뀌어야 함 -> USER_PAUSE 사용
     */
    fun pauseByUser() {
        RunningStateManager.pause(PauseType.USER_PAUSE)

        // 사용자가 직접 멈췄으므로 타이머와 위치 추적 모두 중단 (배터리 절약)
        stopTimer()
        stopTracking()

        notificationHelper.showPauseNotification("일시정지", calculateDistanceString())
    }

    /**
     * 러닝 종료
     */
    fun stopRunning() {
        RunningStateManager.setRunningState(false)
        stopTimer()
        stopTracking()
        notificationHelper.stopNotification()

        scope.launch {
            runningDataSource.finishRun()
            // ViewModel에서 서버 업로드 성공 여부에 따라 discardRun 호출 결정
        }
    }

    private fun startLocationTracking() {
        trackingJob?.cancel()
        trackingJob = locationTracker.startTracking()
            .onEach { location ->
                // 1. 이동 패턴 분석 (정지, 이동, 과속)
                val analysisResult = movementAnalyzer.analyze(location)

                // 2. 상태 변경 발생 시 처리 핸들러 호출
                if (analysisResult.isStatusChanged) {
                    handleStatusChange(analysisResult.status)
                }

                // 3. 거리 계산 및 DB 저장 로직
                // '러닝 중'이고 실제로 '이동 중'일 때만 기록
                val isRunning = RunningStateManager.isRunning.value
                val isMoving = analysisResult.status == MovementStatus.MOVING

                if (isRunning && isMoving) {
                    processRunningLocation(location)
                } else {
                    // 기록은 안 하지만 지도에 현재 위치는 보여주기 위해 업데이트
                    lastLocation = location
                    RunningStateManager.updateLocation(location, 0.0)
                }
            }.launchIn(scope)
    }

    private suspend fun handleStatusChange(newStatus: MovementStatus) {
        // 이미 사용자가 일시정지했거나 과속으로 멈춘 상태라면 자동 로직 무시 (단, 과속 감지는 계속 수행)
        val currentPauseType = RunningStateManager.pauseType.value

        // [예외] 사용자가 멈춰놨는데 갑자기 차 타고 이동할 수도 있으므로 VEHICLE 체크는 항상 수행
        if (newStatus == MovementStatus.VEHICLE) {
            handleVehicleDetection()
            return
        }

        // 사용자가 멈춘 상태(USER_PAUSE)거나 과속 정지(AUTO_PAUSE_VEHICLE) 상태면
        // 휴식/이동 자동 감지 로직 동작 안 함
        if (!RunningStateManager.isRunning.value &&
            (currentPauseType == PauseType.USER_PAUSE || currentPauseType == PauseType.AUTO_PAUSE_VEHICLE)
        ) {
            return
        }

        when (newStatus) {
            MovementStatus.STOPPED -> handleAutoPauseRest()
            MovementStatus.MOVING -> handleAutoResumeMoving()
            else -> {}
        }
    }

    /**
     * [CASE 2] 과속 감지 로직
     * - 기존 레거시 유지: 1회 경고, 2회 강제 일시정지
     */
    private fun handleVehicleDetection() {
        RunningStateManager.incrementVehicleWarningCount()
        val count = RunningStateManager.vehicleWarningCount.value

        if (count >= 2) {
            // 2회 이상: 강제 종료
            performAutoPause(PauseType.AUTO_PAUSE_VEHICLE)
            notificationHelper.forcedStopVehicle()
        } else {
            // 1회: 경고 및 일시정지
            performAutoPause(PauseType.AUTO_PAUSE_VEHICLE)
            notificationHelper.warnVehicle()
        }
    }

    /**
     * 이동 감지 안됨 -> 자동 일시정지 상태로 전이
     */
    private suspend fun handleAutoPauseRest() {
        if (settingsRepository.isAutoPauseEnabled()) {
            performAutoPause(PauseType.AUTO_PAUSE_REST)
            notificationHelper.showPauseNotification("휴식 중", calculateDistanceString())
        }
    }

    /**
     * 자동 일시정지 상태에서 이동 감지됨 -> 자동 재개
     */
    private fun handleAutoResumeMoving() {
        if (
            !RunningStateManager.isRunning.value
            && RunningStateManager.pauseType.value == PauseType.AUTO_PAUSE_REST
        ) {
            resumeRunning()
        }
    }

    private fun performAutoPause(type: PauseType) {
        RunningStateManager.pause(type)
        stopTimer()
    }

    private fun processRunningLocation(location: LocationModel) {
        val lastLoc = lastLocation
        if (lastLoc != null) {
            val dist = DistanceCalculator.calculateDistance(lastLoc, location)
            // 2m 이상 이동 시 기록
            if (dist >= 2.0) {
                RunningStateManager.updateLocation(location, dist)
                RunningStateManager.addPathPoint(location)
                lastLocation = location

                val totalDist = RunningStateManager.totalDistanceMeters.value
                val duration = RunningStateManager.durationSeconds.value

                scope.launch {
                    runningDataSource.saveLocation(location, totalDist, duration)
                }
            }
        } else {
            // 첫 위치
            lastLocation = location
            RunningStateManager.updateLocation(location, 0.0)
            RunningStateManager.addPathPoint(location)

            scope.launch {
                runningDataSource.saveLocation(
                    location,
                    0.0,
                    RunningStateManager.durationSeconds.value
                )
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive && RunningStateManager.isRunning.value) {
                delay(1000L)
                val currentSec = RunningStateManager.durationSeconds.value + 1
                RunningStateManager.updateDuration(currentSec)
                updateNotificationUI()
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun stopTracking() {
        trackingJob?.cancel()
    }

    private fun updateNotificationUI() {
        notificationHelper.updateRunningNotification(
            TimeFormatter.formatSecondsToTime(RunningStateManager.durationSeconds.value),
            calculateDistanceString()
        )
    }

    private fun calculateDistanceString(): String {
        val dist = RunningStateManager.totalDistanceMeters.value
        return "%.2f km".format(dist / 1000.0)
    }
}
