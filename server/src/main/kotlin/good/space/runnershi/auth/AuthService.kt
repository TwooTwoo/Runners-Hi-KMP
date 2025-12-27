package good.space.runnershi.auth

import good.space.runnershi.global.exception.DuplicateEmailException
import good.space.runnershi.global.exception.DuplicateNameException
import good.space.runnershi.global.exception.InvalidCredentialsException
import good.space.runnershi.global.exception.InvalidTokenException
import good.space.runnershi.global.exception.SocialLoginRestrictedException
import good.space.runnershi.global.exception.UserNotFoundException
import good.space.runnershi.global.security.JwtPlugin
import good.space.runnershi.model.dto.auth.LoginRequest
import good.space.runnershi.model.dto.auth.SignUpRequest
import good.space.runnershi.model.dto.auth.TokenRefreshResponse
import good.space.runnershi.model.dto.auth.TokenResponse
import good.space.runnershi.user.domain.LocalUser
import good.space.runnershi.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtPlugin: JwtPlugin,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    @Transactional
    fun signUp(request: SignUpRequest) {
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateEmailException()
        }

        if (userRepository.existsByName(request.name)) {
            throw DuplicateNameException()

        }

        val encodedPassword = passwordEncoder.encode(request.password)
        val newUser = LocalUser(
            email = request.email,
            name = request.name,
            password = encodedPassword,
            sex = request.sex
        )

        userRepository.save(newUser)
    }

    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw InvalidCredentialsException()

        if (user !is LocalUser) {
            throw SocialLoginRestrictedException()
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidCredentialsException()
        }

        val accessToken = jwtPlugin.generateAccessToken(
            subject = user.id.toString(), // 토큰 주인(ID)
            email = user.email,
            role = user.userType.name     // "LOCAL"
        )

        val refreshToken = jwtPlugin.generateRefreshToken(
            subject = user.id.toString(),
            email = user.email,
            role = user.userType.name
        )

        val existingToken = refreshTokenRepository.findByUserId(user.id!!)

        if (existingToken != null) {
            existingToken.updateToken(refreshToken)
        } else {
            refreshTokenRepository.save(RefreshToken(user = user, token = refreshToken))
        }

        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    @Transactional
    fun refreshAccessToken(refreshToken: String): TokenRefreshResponse {
        val verifiedToken = jwtPlugin.validateToken(refreshToken)
            .getOrElse { throw InvalidTokenException("유효하지 않은 Refresh Token입니다.") }

        val userId = verifiedToken.body.subject.toLong()
        val savedToken = refreshTokenRepository.findByUserId(userId)
            ?: throw InvalidTokenException("로그아웃된 사용자입니다.")

        if (savedToken.token != refreshToken) {
            throw InvalidTokenException("토큰이 일치하지 않습니다.")
        }

        val user = userRepository.findById(userId)
            .orElseThrow { UserNotFoundException() }

        val newAccessToken = jwtPlugin.generateAccessToken(
            subject = user.id.toString(),
            email = user.email,
            role = user.userType.name
        )

        return TokenRefreshResponse(newAccessToken, refreshToken)
    }

    @Transactional
    fun logout(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
