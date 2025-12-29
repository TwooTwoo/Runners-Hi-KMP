package good.space.runnershi.percentile.service

import good.space.runnershi.model.dto.running.percentile.RunningResultPercentileRequest
import good.space.runnershi.model.dto.running.percentile.RunningResultPercentileResponse
import good.space.runnershi.percentile.repository.RunningResultReferenceRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.roundToInt

@Service
class RunningResultPercentileService(
    private val runningResultReferenceRepository: RunningResultReferenceRepository
) {
    @Transactional(readOnly = true)
    fun calculate(req: RunningResultPercentileRequest): RunningResultPercentileResponse {
        val bucket = distanceBucket(req.totalDistanceMeters.toInt())
        val myPace = paceSecPerKm(req.totalDistanceMeters.toInt(), req.durationSec)

        val total = runningResultReferenceRepository.countTotal(bucket)

        if (total == 0L) {
            return RunningResultPercentileResponse(
                topPercent = null
            )
        }

        val faster = runningResultReferenceRepository.countFasterThanMe(bucket, myPace)

        val topPercentRaw = ( faster.toDouble() / total.toDouble()) * 100.0

        return RunningResultPercentileResponse(
            topPercent = formatPercent(topPercentRaw)
        )

    }

    private fun formatPercent(value: Double): String {
        return String.format("%.0f", value)
    }

    private fun paceSecPerKm(distanceM: Int, durationSec: Int): Int {
        if (distanceM <= 0 || durationSec <= 0) return 0
        val distanceKm = distanceM / 1000.0
        return (durationSec / distanceKm).roundToInt()
    }

    private fun distanceBucket(distanceM: Int): String {
        val distanceKm = distanceM / 1000.0
        return when {
            distanceKm < 2 -> "1-2"
            distanceKm < 4 -> "2-4"
            distanceKm < 6 -> "4-6"
            distanceKm < 8 -> "6-8"
            distanceKm < 10 -> "8-10"
            distanceKm < 12 -> "10-12"
            distanceKm < 14 -> "12-14"
            distanceKm < 16 -> "14-16"
            distanceKm < 18 -> "16-18"
            distanceKm < 20 -> "18-20"
            distanceKm < 25 -> "20-25"
            distanceKm < 30 -> "25-30"
            distanceKm < 35 -> "30-35"
            distanceKm < 40 -> "35-40"
            distanceKm < 42.195 -> "40-42.195"
            else -> "42.195+"
        }
    }
}

