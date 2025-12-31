package good.space.runnershi.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class AndroidServiceLauncher(
    private val context: Context
) : ServiceLauncher {

    override fun startService() {
        // 위치 권한 체크
        val hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!hasLocationPermission) {
            throw SecurityException(
                "Location permission is required to start the running service. " +
                "Please grant location permission first."
            )
        }
        
        val intent = Intent(context, RunningService::class.java).apply {
            action = "ACTION_START"
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    override fun pauseService() {
        sendCommand("ACTION_PAUSE")
    }

    override fun resumeService() {
        sendCommand("ACTION_RESUME")
    }

    override fun stopService() {
        sendCommand("ACTION_STOP")
    }

    private fun sendCommand(action: String) {
        val intent = Intent(context, RunningService::class.java).apply {
            this.action = action
        }
        context.startService(intent)
    }
}
