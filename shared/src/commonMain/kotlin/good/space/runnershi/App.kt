package good.space.runnershi

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import good.space.runnershi.model.dto.running.UpdatedUserResponse
import good.space.runnershi.ui.home.HomeRoute
import good.space.runnershi.ui.login.LoginRoute
import good.space.runnershi.ui.navigation.Screen
import good.space.runnershi.ui.result.ResultRoute
import good.space.runnershi.ui.running.RunningResultToShow
import good.space.runnershi.ui.running.RunningRoute
import good.space.runnershi.ui.signup.SignUpRoute
import good.space.runnershi.ui.theme.RunnersHiTheme
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        RunnersHiTheme {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Login.name
            ) {
                // 로그인 화면
                composable(route = Screen.Login.name) {
                    LoginRoute(
                        navigateToHome = {
                            navController.navigate(Screen.Home.name) {
                                popUpTo(Screen.Login.name) { inclusive = true }
                            }
                        },
                        navigateToSignUp = {
                            navController.navigate(Screen.SignUp.name)
                        }
                    )
                }

                // 회원가입 화면
                composable(route = Screen.SignUp.name) {
                    SignUpRoute(
                        navigateBack = {
                          navController.popBackStack() // 뒤로 가기
                        },
                        navigateToHome = {
                            navController.navigate(Screen.Home.name)
                        }
                    )
                }

                // 홈 화면
                composable(route = Screen.Home.name) {
                    HomeRoute(
                        navigateToRunning = {
                            navController.navigate(Screen.RUNNING.name)
                        }
                    )
                }

                // 러닝 화면
                composable(route = Screen.RUNNING.name) {
                    RunningRoute(
                        navigateToResult = { userInfo, runResult ->
                            navController.currentBackStackEntry?.savedStateHandle?.apply {
                                set("userInfo", userInfo)
                                set("runResult", runResult)
                            }
                            navController.navigate(Screen.RESULT.name) {
                                popUpTo(Screen.Home.name)
                            }
                        }
                    )
                }

                // 결과 화면
                composable(route = Screen.RESULT.name) {
                    val previousBackStack = navController.previousBackStackEntry
                    val userInfo = previousBackStack?.savedStateHandle?.get<UpdatedUserResponse>("userInfo")
                    val runResult = previousBackStack?.savedStateHandle?.get<RunningResultToShow>("runResult")

                    // 데이터가 정상적으로 넘어왔을 때만 화면 표시
                    if (runResult != null) {
                        ResultRoute(
                            userInfo = userInfo,
                            runResult = runResult,
                            onCloseClick = {
                                navController.navigate(Screen.Home.name) {
                                    popUpTo(Screen.Home.name) { inclusive = true }
                                }
                            }
                        )
                    } else {
                        // 데이터가 없으면 홈으로 튕겨내기
                        navController.navigate(Screen.Home.name)
                    }
                }
            }
        }
    }
}
