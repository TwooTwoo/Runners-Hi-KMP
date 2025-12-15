package good.space.runnershi.global.running

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne

@Entity
class Point (
    val latitude: Double, //위도
    val longitude: Double, //경도
    val sequenceOrder: Int,
    @ManyToOne
    val route: Route,
){
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}