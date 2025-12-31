package good.space.runnershi.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar // TODO: 실제 디자인 아이콘으로 교체 필요
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import good.space.runnershi.ui.components.GradientCircleButtonColor.*
import good.space.runnershi.ui.components.GradientCircleButtonIcon.*
import good.space.runnershi.ui.theme.RunnersHiTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import runnershi.shared.generated.resources.Res
import runnershi.shared.generated.resources.car
import runnershi.shared.generated.resources.logo

@Composable
fun VehicleWarningDialog(
    isForcedStop: Boolean,
    onResumeClick: () -> Unit,
    onFinishClick: () -> Unit
) {
    // 반투명 배경
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f) // 너비를 조금 더 넓게 조정
                .padding(20.dp),
            shape = RoundedCornerShape(24.dp), // 모서리를 조금 더 둥글게
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 40.dp, horizontal = 24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CarIcon()

                Spacer(modifier = Modifier.height(20.dp))

                WarningText(isForcedStop)

                Spacer(modifier = Modifier.height(30.dp))

                ActionButton(
                    isForcedStop = isForcedStop,
                    onFinishClick = onFinishClick,
                    onResumeClick =  onResumeClick
                )
            }
        }
    }
}

@Composable
private fun CarIcon() {
    Image(
        painter = painterResource(Res.drawable.car),
        contentDescription = "Car",
        modifier = Modifier
            .size(100.dp)
    )
}

@Composable
private fun WarningText(
    isForcedStop: Boolean
) {
    val messageText = if (isForcedStop) {
        "이동수단 탑승이 여러 번 감지되어서\n러닝을 중단했어요"
    } else {
        "속도가 너무 빨라서 잠깐 멈췄어요\n혹시 이동수단에 탑승하셨나요?"
    }

    Text(
        text = messageText,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RunnersHiTheme.colorScheme.onSurface,
            lineHeight = 26.sp
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun ActionButton(
    isForcedStop: Boolean,
    onFinishClick: () -> Unit,
    onResumeClick: () -> Unit
) {
    val buttonIcon = if (isForcedStop) STOP else START
    val buttonColor = if (isForcedStop) BLACK else GREEN
    val buttonAction = if (isForcedStop) onFinishClick else onResumeClick

    GradientCircleButton(
        buttonColor = buttonColor,
        buttonIcon = buttonIcon,
        onClick = buttonAction,
    )
}

@Preview
@Composable
fun VehicleWarningDialogResumePreview() {
    RunnersHiTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            VehicleWarningDialog(
                isForcedStop = false,
                onResumeClick = {},
                onFinishClick = {}
            )
        }
    }
}

@Preview
@Composable
fun VehicleWarningDialogFinishPreview() {
    RunnersHiTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Gray)
        ) {
            VehicleWarningDialog(
                isForcedStop = true,
                onResumeClick = {},
                onFinishClick = {}
            )
        }
    }
}
