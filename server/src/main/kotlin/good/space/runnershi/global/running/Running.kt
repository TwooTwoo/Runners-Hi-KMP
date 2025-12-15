package good.space.runnershi.global.running

import good.space.runnershi.user.domain.User
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Running (
    var durationSeconds: Long,
    var distanceMeters: Double,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @ManyToOne
    var user: User? = null

    var averagePace: Double = if (distanceMeters > 0 && durationSeconds > 0) {
        1000 / (distanceMeters / durationSeconds.toDouble())
    } else {
        0.0
    }
}