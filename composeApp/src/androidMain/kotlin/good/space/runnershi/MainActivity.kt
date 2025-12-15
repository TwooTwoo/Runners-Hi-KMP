package good.space.runnershi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import good.space.runnershi.service.AndroidServiceController
import good.space.runnershi.shared.di.androidPlatformModule
import good.space.runnershi.shared.di.initKoin
import good.space.runnershi.ui.screen.RunResultScreen
import good.space.runnershi.ui.screen.RunningScreen
import good.space.runnershi.viewmodel.RunningViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initKoin(extraModules = listOf(androidPlatformModule))
        
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // 1. 의존성 주입 (수동)
        val serviceController = AndroidServiceController(this)
        val viewModel = RunningViewModel(serviceController)

        setContent {
            MaterialTheme {
                // 2. 화면 전환 처리
                AppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AppContent(viewModel: RunningViewModel) {
    val runResult by viewModel.runResult.collectAsState()

    // runResult 데이터가 있으면 결과 화면을, 없으면 러닝 화면을 보여줌
    if (runResult != null) {
        RunResultScreen(
            result = runResult!!,
            onClose = { viewModel.closeResultScreen() }
        )
    } else {
        RunningScreen(viewModel = viewModel)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    MaterialTheme {
        // Preview용 더미 ViewModel (실제로는 사용하지 않음)
    }
}