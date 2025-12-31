package good.space.runnershi.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android 위치 권한 관리 구현체
 */
class AndroidLocationPermissionManager(
    private val context: Context
) : LocationPermissionManager {
    
    private val _hasPermission = MutableStateFlow(checkPermission())
    override val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()
    
    override fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun requestPermission(): Boolean {
        // Activity가 필요하므로 Compose에서 처리
        // 이 메서드는 실제로는 사용되지 않고, Compose에서 직접 처리
        return checkPermission()
    }
    
    fun updatePermissionState() {
        _hasPermission.value = checkPermission()
    }
}

/**
 * Compose에서 사용할 수 있는 권한 요청 헬퍼
 */
@Composable
fun rememberLocationPermissionManager(): LocationPermissionManager {
    val context = LocalContext.current
    val manager = remember {
        AndroidLocationPermissionManager(context)
    }
    return manager
}

/**
 * Compose에서 권한 요청을 위한 런처 생성
 */
@Composable
fun rememberLocationPermissionLauncher(
    onPermissionResult: (Boolean) -> Unit
): (() -> Unit) {
    val context = LocalContext.current
    val manager = rememberLocationPermissionManager()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        
        (manager as? AndroidLocationPermissionManager)?.updatePermissionState()
        onPermissionResult(granted)
    }
    
    return remember {
        {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}

/**
 * RunningRoute에서 사용할 권한 처리 Composable
 * Android에서만 동작하며, 권한이 없으면 자동으로 요청
 */
@Composable
actual fun HandleLocationPermission(
    permissionManager: LocationPermissionManager
) {
    val androidManager = permissionManager as? AndroidLocationPermissionManager ?: return
    val hasPermission = permissionManager.hasPermission.value
    
    val requestPermission = rememberLocationPermissionLauncher { granted ->
        // 권한 결과는 StateFlow를 통해 자동으로 반영됨
    }
    
    // 권한이 없으면 자동으로 요청
    androidx.compose.runtime.LaunchedEffect(hasPermission) {
        if (!hasPermission) {
            requestPermission()
        }
    }
}

