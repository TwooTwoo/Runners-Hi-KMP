package good.space.runnershi.model.dto.running.percentile

import kotlinx.serialization.Serializable

@Serializable
data class RunningResultPercentileResponse(
    val topPercent: String?,
)
