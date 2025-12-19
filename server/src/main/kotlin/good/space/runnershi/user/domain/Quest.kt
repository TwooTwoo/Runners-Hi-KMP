package good.space.runnershi.user.domain

import good.space.runnershi.model.dto.running.RunCreateRequest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

enum class Quest(
    val title: String,
    val level: Long,
    val available: (RunCreateRequest) -> Boolean,
    val exp: Long
) {
    DISTANCE_LV1(
        "2Km 달리기",
        1,
        { running -> running.distanceMeters >= 2_000 },
        100
    ),

    DURATION_LV1(
        "20분간 땀 흘리기",
        1,
        { run -> run.totalDuration.inWholeMinutes >= 20 },
        100
    ),

    MORNING_LV1(
        "상쾌한 아침의 시작",
        1,
        { run -> val hour = run.startedAt.toLocalDateTime(TimeZone.currentSystemDefault()).hour
            hour in 6..9
        },
        100
    ),

    NONSTOP_LV1(
        "멈추지 않는 심장",
        1,
        { run ->
            val restTime = run.totalDuration - run.runningDuration
            run.distanceMeters >= 3_000 && restTime.inWholeMinutes < 0.1
        },
        100
    ),

    SPEED_LV1(
        "바람을 가르는 속도",
        1,
        { run ->
            val paceSeconds = run.runningDuration.inWholeSeconds / (run.distanceMeters / 1000.0)
            run.distanceMeters < 1_000 && paceSeconds <= 420
        },
        100
    ),


}
