package good.space.runnershi.global.running.mapper // 패키지는 적절히 설정

import good.space.runnershi.model.dto.running.LongestDistance
import good.space.runnershi.user.domain.User

fun User.toLongestDistanceDto(): LongestDistance {
    return LongestDistance(
        longestDistance = this.longestDistanceMeters
    )
}
