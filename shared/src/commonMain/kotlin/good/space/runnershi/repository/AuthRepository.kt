package good.space.runnershi.repository

import good.space.runnershi.model.dto.LoginRequest
import good.space.runnershi.model.dto.LoginResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): Result<LoginResponse>
}