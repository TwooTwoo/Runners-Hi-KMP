package good.space.runnershi.global.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.Date

@Component
class JwtPlugin (
    @Value("\${jwt.secret}")private val secret: String,
    @Value("\${jwt.access-token-validity-in-milliseconds}") private val expiration: Long,
    @Value("\${jwt.refresh-duration}") private val refreshTokenExpirationHour: Long
){
    fun generateRefreshToken(subject: String, email: String, role: String): String {
        return generateToken(subject, email, role, Duration.ofMillis(refreshTokenExpirationHour))
    }

    fun generateAccessToken(subject: String, email: String, role: String): String {
        return generateToken(subject, email, role, Duration.ofMillis(expiration))
    }

    private val key by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    private fun generateToken(subject: String, email: String, role: String, expirationPeriod: Duration): String {
        val now = Instant.now()

        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(expirationPeriod)))
            .addClaims(mapOf("email" to email, "role" to role))
            .signWith(key)
            .compact()
    }

    fun validateToken(jwt: String): Result<Jws<Claims>> {
        return runCatching {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt)
        }
    }
}