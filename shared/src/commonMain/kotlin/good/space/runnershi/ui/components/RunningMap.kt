package good.space.runnershi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import good.space.runnershi.model.domain.location.LocationModel

@Composable
expect fun RunningMap(
    currentLocation: LocationModel?,
    pathSegments: List<List<LocationModel>>,
    modifier: Modifier = Modifier
)
