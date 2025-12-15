package good.space.runnershi.global.security
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic { it.disable() } // UI 사용하는 기본 로그인 비활성화
            .csrf { it.disable() }      // CSRF 보안 비활성화 (JWT는 필요 없음)
            .formLogin { it.disable() } // 폼 로그인 비활성화
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안 함
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/auth/signup", // 회원가입
                    "/api/v1/auth/login",  // 로그인
                    "/swagger-ui/**",      // 스웨거 (선택)
                    "/v3/api-docs/**"      // 스웨거 (선택)
                ).permitAll() // 위 주소는 누구나 접근 가능
                    .anyRequest().authenticated() // 나머지는 다 로그인해야 접근 가능
            }
            .build()
    }

    // 비밀번호 암호화 도구 (회원가입/로그인 서비스에서 주입받아 사용)
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}