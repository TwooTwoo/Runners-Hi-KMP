package good.space.runnershi.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AndroidNotificationHelper(
    private val context: Context
) : NotificationHelper {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_ID = "running_channel"
        const val NOTIFICATION_ID = 1 // 포그라운드 서비스 ID와 일치해야 함
    }

    init {
        createNotificationChannel()
    }

    override fun startRunningNotification(time: String, distance: String) {
        val notification = buildNotification(
            title = "Runner's Hi - 러닝 중 🏃",
            content = "시간: $time | 거리: $distance"
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun updateRunningNotification(time: String, distance: String) {
        // 기존 알림을 갱신합니다. ID가 같으면 내용은 업데이트되고 알림음은 다시 울리지 않습니다.
        startRunningNotification(time, distance)
    }

    override fun showPauseNotification(title: String, content: String) {
        val notification = buildNotification(title, content)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun warnVehicle() {
        val notification = buildNotification(
            title = "⚠️ 과속 감지 (1/2)",
            content = "차량 이동이 감지되어 일시정지합니다."
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun forcedStopVehicle() {
        val notification = buildNotification(
            title = "🚫 러닝 강제 종료",
            content = "반복된 차량 이동으로 러닝을 종료합니다."
        )
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    override fun stopNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    // --- 내부 헬퍼 메서드 ---

    private fun buildNotification(title: String, content: String): Notification {
        val openAppIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            // 기존 플래그 유지
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        } ?: Intent()

        val pendingIntentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, openAppIntent, pendingIntentFlag
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: 앱의 실제 아이콘 리소스로 변경 (R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true) // 사용자가 스와이프해서 지울 수 없게 설정 (포그라운드 서비스 필수)
            .setOnlyAlertOnce(true) // 내용이 업데이트될 때마다 소리/진동 울리지 않음
            .setPriority(NotificationCompat.PRIORITY_LOW) // 중요도 낮음 (시각적 방해 최소화)

        // 안드로이드 12 이상에서 포그라운드 서비스 알림이 즉시 보이도록 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 채널이 이미 존재하면 다시 생성하지 않습니다.
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "러닝 추적 알림", // 사용자에게 보이는 채널 이름
                    NotificationManager.IMPORTANCE_LOW // 알림음 없이 조용히 표시
                ).apply {
                    description = "실시간 러닝 상태를 표시합니다."
                    setShowBadge(false)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}
