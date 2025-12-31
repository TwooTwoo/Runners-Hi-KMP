package good.space.runnershi.util

interface NotificationHelper {
    fun startRunningNotification(time: String, distance: String)
    fun updateRunningNotification(time: String, distance: String)
    fun showPauseNotification(title: String, content: String)
    fun warnVehicle() // 1회차 경고
    fun forcedStopVehicle() // 2회차 강제 종료
    fun stopNotification()
}
