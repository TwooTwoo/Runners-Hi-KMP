package good.space.runnershi.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import good.space.runnershi.model.domain.location.LocationModel
import good.space.runnershi.util.MapsApiKeyChecker
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
actual fun RunningMap(
    currentLocation: LocationModel?,
    pathSegments: List<List<LocationModel>>,
    modifier: Modifier
) {
    val context = LocalContext.current
    val isInspection = LocalInspectionMode.current
    val isApiKeySet = remember {
        isInspection || MapsApiKeyChecker.isApiKeySet(context)
    }

    val hasLocationPermission = remember(context) { checkLocationPermission(context) }

    val cameraPositionState = rememberCameraPositionState()

    // pathSegments가 바뀔 때만 변환 수행
    val polylines by remember(pathSegments) {
        derivedStateOf {
            pathSegments.map { segment ->
                segment.map { LatLng(it.latitude, it.longitude) }
            }
        }
    }

    CameraAutoTracking(
        currentLocation = currentLocation,
        cameraPositionState = cameraPositionState
    )

    Box(modifier = modifier) {
        if (isApiKeySet) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                // 권한이 있을 때만 내 위치 활성화
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = hasLocationPermission,
                    zoomControlsEnabled = false,
                    compassEnabled = true
                )
            ) {
                // 미리 변환해둔 polylines 사용
                polylines.forEach { points ->
                    if (points.isNotEmpty()) {
                        Polyline(
                            points = points,
                            color = Color(0xFF6200EE),
                            width = 15f,
                            jointType = JointType.ROUND,
                            startCap = RoundCap(),
                            endCap = RoundCap()
                        )
                    }
                }
            }
        } else {
            NoApiKeyPlaceholder()
        }
    }
}

/**
 * 위치 권한(Fine 또는 Coarse)이 있는지 확인한다
 */
private fun checkLocationPermission(context: Context): Boolean {
    val hasFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return hasFineLocation || hasCoarseLocation
}

@Composable
private fun CameraAutoTracking(
    currentLocation: LocationModel?,
    cameraPositionState: CameraPositionState
) {
    var lastAnimatedLocation by remember { mutableStateOf<LatLng?>(null) }

    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->
            val currentLatLng = LatLng(loc.latitude, loc.longitude)

            // 사용자가 지도를 터치해서 움직이고 있지 않을 때만 자동 이동
            if (!cameraPositionState.isMoving && shouldAnimateCamera(lastAnimatedLocation, currentLatLng)) {
                lastAnimatedLocation = currentLatLng
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(currentLatLng, 17f)
                    ),
                    1000
                )
            }
        }
    }
}

private fun shouldAnimateCamera(lastLocation: LatLng?, currentLocation: LatLng): Boolean {
    if (lastLocation == null) return true

    val latDiff = currentLocation.latitude - lastLocation.latitude
    val lngDiff = currentLocation.longitude - lastLocation.longitude
    val distance = sqrt(latDiff.pow(2.0) + lngDiff.pow(2.0))

    return distance > 0.0001
}

@Composable
private fun NoApiKeyPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️ Google Maps API Key 필요",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6200EE),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RunningMapPreview() {
    val startLat = 37.5665
    val startLng = 126.9780

    val dummyPath = listOf(
        listOf(
            LocationModel(startLat, startLng, 0, 0.0F),
            LocationModel(startLat + 0.001, startLng, 0, 0.0F),
            LocationModel(startLat + 0.001, startLng + 0.001, 0, 0.0F),
            LocationModel(startLat + 0.002, startLng + 0.001, 0, 0.0F)
        )
    )

    // 현재 위치
    val currentLocation = LocationModel(startLat + 0.002, startLng + 0.001, 0, 0.0F)

    MaterialTheme {
        RunningMap(
            currentLocation = currentLocation,
            pathSegments = dummyPath,
            modifier = Modifier.fillMaxSize()
        )
    }
}
