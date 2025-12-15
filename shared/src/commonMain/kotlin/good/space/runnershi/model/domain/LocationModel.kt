package good.space.runnershi.model.domain

// UI나 비즈니스 로직에서 사용할 순수 위치 데이터
data class LocationModel(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double = 0.0,
    val timestamp: Long
)

