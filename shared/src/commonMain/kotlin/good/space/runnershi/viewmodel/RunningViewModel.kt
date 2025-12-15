package good.space.runnershi.viewmodel

import good.space.runnershi.location.LocationTracker
import good.space.runnershi.model.domain.LocationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RunningViewModel(
    private val locationTracker: LocationTracker // 인터페이스 주입
) {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val _currentLocation = MutableStateFlow<LocationModel?>(null)
    val currentLocation: StateFlow<LocationModel?> = _currentLocation.asStateFlow()

    fun startRun() {
        scope.launch {
            locationTracker.startTracking().collect { location ->
                // UI 상태 업데이트
                _currentLocation.value = location
                
                // TODO: 거리 계산, 페이스 계산 등 비즈니스 로직 추가
                // calculateDistance(location)
            }
        }
    }
}

