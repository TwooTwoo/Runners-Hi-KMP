package good.space.runnershi.repository

import good.space.runnershi.model.dto.auth.LoginRequest
import good.space.runnershi.model.dto.auth.LoginResponse
import good.space.runnershi.model.dto.auth.SignUpRequest
import kotlinx.coroutines.delay

class MockAuthRepository : AuthRepository {

    override suspend fun login(request: LoginRequest): Result<LoginResponse> {
        delay(1000) // 네트워크 지연 시뮬레이션
        return Result.success(LoginResponse("mock_access_token", "mock_refresh_token"))
    }

    override suspend fun signUp(request: SignUpRequest): Result<LoginResponse> {
        delay(1500) // 회원가입은 좀 더 오래 걸리는 척
        println("📡 [Mock Server] User Created: ${request.email} / ${request.name}")
        return Result.success(
            LoginResponse(
                accessToken = "mock_access_token_signup",
                refreshToken = "mock_refresh_token_signup"
            )
        )
    }

    override suspend fun logout(): Result<Unit> {
        delay(500) // 네트워크 지연 시뮬레이션
        println("📡 [Mock Server] Logout")
        return Result.success(Unit)
    }
}

