package good.space.runnershi.global.auth

data class SignupRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)