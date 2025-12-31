package good.space.runnershi.permission

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * iOS 위치 권한 관리 구현체
 * iOS에서는 별도 권한 요청 로직이 필요하지만, 일단은 항상 true 반환
 */
class IosLocationPermissionManager : LocationPermissionManager {
    private val _hasPermission = MutableStateFlow(true)
    override val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()
    
    override fun checkPermission(): Boolean = true
    
    override suspend fun requestPermission(): Boolean = true
}

/**
 * iOS에서는 권한 처리가 다르므로 빈 구현
 */
@Composable
actual fun HandleLocationPermission(
    permissionManager: LocationPermissionManager
) {
    // iOS에서는 별도 처리 불필요
}
