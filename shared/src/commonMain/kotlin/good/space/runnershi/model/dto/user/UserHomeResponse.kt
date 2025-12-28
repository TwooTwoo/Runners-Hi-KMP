package good.space.runnershi.model.dto.running

import good.space.runnershi.model.domain.auth.Sex
import kotlinx.serialization.Serializable

@Serializable
data class UserHomeResponse(
    val userId: Long,
    val name: String,
    val userExp: Long,
    val totalDistance: Double,
    val totalRunningDays: Long,
    val dailyQuests: List<HomeQuestInfo>,
    val achievements: List<String>,
    val sex: Sex,
    val level: Int,
    val avatars: List<String>,
)

@Serializable
data class HomeQuestInfo(
    val questId: Long,
    val title: String,
    val level: Long,
    val exp: Long,
    val isCompleted: Boolean
)
