package good.space.runnershi.auth

import good.space.runnershi.global.exception.DuplicateEmailException
import good.space.runnershi.global.exception.DuplicateNameException
import good.space.runnershi.global.security.JwtPlugin
import good.space.runnershi.user.repository.UserRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.crypto.password.PasswordEncoder

@ExtendWith(MockitoExtension::class)
class AuthServiceTest {
    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var jwtPlugin: JwtPlugin

    @Mock
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @InjectMocks
    private lateinit var authService: AuthService

    @Test
    @DisplayName("이메일 중복 체크 성공 시: 예외가 발생하지 않아야 한다")
    fun checkEmailDuplicate_Success() {
        val email = "test@example.com"

        `when`(userRepository.existsByEmail(email)).thenReturn(false)

        authService.checkEmailDuplicate(email)

        verify(userRepository).existsByEmail(email)
    }

    @Test
    @DisplayName("이메일 중복 체크 시 중복된 이메일인 경우: DuplicateEmailException이 발생해야 한다")
    fun checkEmailDuplicate_Duplicate() {
        val email = "duplicate@example.com"

        `when`(userRepository.existsByEmail(email)).thenReturn(true)

        assertThatThrownBy {
            authService.checkEmailDuplicate(email)
        }
            .isInstanceOf(DuplicateEmailException::class.java)
            .hasMessageContaining("이미 사용 중인 이메일입니다.")

        verify(userRepository).existsByEmail(email)
    }

    @Test
    @DisplayName("이름 중복 체크 성공 시: 예외가 발생하지 않아야 한다")
    fun checkNameDuplicate_Success() {
        val name = "TestUser"

        `when`(userRepository.existsByName(name)).thenReturn(false)

        authService.checkNameDuplicate(name)

        verify(userRepository).existsByName(name)
    }

    @Test
    @DisplayName("이름 중복 체크 시 중복된 이름인 경우: DuplicateNameException이 발생해야 한다")
    fun checkNameDuplicate_Duplicate() {
        val name = "DuplicateUser"

        `when`(userRepository.existsByName(name)).thenReturn(true)

        assertThatThrownBy {
            authService.checkNameDuplicate(name)
        }
            .isInstanceOf(DuplicateNameException::class.java)
            .hasMessageContaining("이미 사용 중인 이름입니다.")

        verify(userRepository).existsByName(name)
    }
}

