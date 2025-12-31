package good.space.runnershi.model.domain

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import good.space.runnershi.model.domain.location.LocationModel
import good.space.runnershi.model.domain.location.LocationTracker
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidLocationTracker(
    context: Context
) : LocationTracker {

    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun startTracking(): Flow<LocationModel> = callbackFlow {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000L // 기본 3초 주기
        ).apply {
            setMinUpdateIntervalMillis(1000L)
            setMinUpdateDistanceMeters(2f)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.forEach { location ->
                    val model = LocationModel(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        timestamp = location.time,
                        speed = location.speed,
                        accuracy = location.accuracy
                    )
                    trySend(model)
                }
            }
        }

        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            close(e) // 스트림 종료 및 에러 전파
        }

        awaitClose {
            client.removeLocationUpdates(locationCallback)
        }
    }

    override fun stopTracking() {
        // Flow가 취소되면 awaitClose가 실행되므로 비워둡니다.
    }
}
