package good.space.runnershi.permission

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.StateFlow

/**
 * 위치 권한 관리 인터페이스
 * 플랫폼별 구현체가 권한 체크 및 요청을 담당
 */
interface LocationPermissionManager {
    /**
     * 현재 위치 권한 상태
     */
    val hasPermission: StateFlow<Boolean>
    
    /**
     * 권한 요청
     * @return 권한이 부여되었는지 여부
     */
    suspend fun requestPermission(): Boolean
    
    /**
     * 권한이 있는지 확인 (동기)
     */
    fun checkPermission(): Boolean
}

/**
 * Compose에서 권한 요청을 처리하는 헬퍼
 * Android에서는 자동으로 권한 요청 다이얼로그 표시
 */
@Composable
expect fun HandleLocationPermission(
    permissionManager: LocationPermissionManager
)
