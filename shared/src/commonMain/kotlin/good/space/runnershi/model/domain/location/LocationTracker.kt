package good.space.runnershi.model.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationTracker {
    // 위치 업데이트를 Flow(스트림) 형태로 제공
    fun startTracking(): Flow<LocationModel>

    // 위치 추적 중단 (필요시 리소스 해제)
    fun stopTracking()
}
