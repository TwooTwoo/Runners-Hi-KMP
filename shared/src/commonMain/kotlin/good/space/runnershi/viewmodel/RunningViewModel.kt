package good.space.runnershi.viewmodel

import good.space.runnershi.location.LocationTracker
import good.space.runnershi.model.domain.LocationModel
import good.space.runnershi.util.DistanceCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RunningViewModel(
    private val locationTracker: LocationTracker
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    // 1. 현재 위치
    private val _currentLocation = MutableStateFlow<LocationModel?>(null)
    val currentLocation: StateFlow<LocationModel?> = _currentLocation.asStateFlow()

    // 2. 총 뛴 거리 (미터)
    private val _totalDistanceMeters = MutableStateFlow(0.0)
    val totalDistanceMeters: StateFlow<Double> = _totalDistanceMeters.asStateFlow()

    // 3. 경로 좌표 리스트 (지도에 선 그리기용)
    private val _pathPoints = MutableStateFlow<List<LocationModel>>(emptyList())
    val pathPoints: StateFlow<List<LocationModel>> = _pathPoints.asStateFlow()

    // 이전 위치를 기억하기 위한 변수
    private var lastLocation: LocationModel? = null

    // GPS 노이즈 필터링 임계값 (예: 2미터 미만 이동은 무시)
    private val MIN_DISTANCE_THRESHOLD = 2.0

    fun startRun() {
        // 러닝 시작 시 리스트 초기화
        _pathPoints.value = emptyList()
        _totalDistanceMeters.value = 0.0
        lastLocation = null

        scope.launch {
            locationTracker.startTracking().collect { newLocation ->
                updateRunData(newLocation)
            }
        }
    }

    private fun updateRunData(newLocation: LocationModel) {
        val lastLoc = lastLocation

        if (lastLoc != null) {
            // 거리 계산 수행
            val distanceDelta = DistanceCalculator.calculateDistance(lastLoc, newLocation)

            // 노이즈 필터링: 의미 있는 거리만큼 이동했는지 확인
            if (distanceDelta >= MIN_DISTANCE_THRESHOLD) {
                _totalDistanceMeters.value += distanceDelta
                lastLocation = newLocation // 유효한 이동일 때만 갱신
                _currentLocation.value = newLocation
                
                // 경로 리스트에 좌표 추가
                // 주의: StateFlow는 객체 참조가 바뀌어야 방출(Emit)되므로, 새 리스트를 만들어 할당합니다.
                val currentList = _pathPoints.value
                _pathPoints.value = currentList + newLocation
            }
        } else {
            // 첫 위치 수신 시
            lastLocation = newLocation
            _currentLocation.value = newLocation
            
            // 시작점 추가
            _pathPoints.value = listOf(newLocation)
        }
    }
    
    fun stopRun() {
        locationTracker.stopTracking()
        // TODO: _pathPoints.value를 DB에 저장하는 로직 필요
    }
}

