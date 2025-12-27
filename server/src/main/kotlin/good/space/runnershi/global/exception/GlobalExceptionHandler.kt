package good.space.runnershi.global.exception
import good.space.runnershi.exception.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

class DuplicateEmailException : RuntimeException("이미 사용 중인 이메일입니다.")
class DuplicateNameException : RuntimeException("이미 사용 중인 이름입니다.")
class InvalidCredentialsException : RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.")
class SocialLoginRestrictedException : RuntimeException("소셜 로그인으로 가입된 계정입니다.")
class InvalidTokenException(message: String = "유효하지 않은 토큰입니다.") : RuntimeException(message)
class UserNotFoundException : RuntimeException("사용자를 찾을 수 없습니다.")

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateEmailException::class, DuplicateNameException::class)
    fun handleDuplicate(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(message = e.message ?: "중복된 데이터입니다.", code = "DUPLICATE_DATA"))
    }
    @ExceptionHandler(InvalidCredentialsException::class, InvalidTokenException::class)
    fun handleAuthFail(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(message = e.message ?: "인증에 실패했습니다.", code = "AUTH_FAIL"))
    }
    @ExceptionHandler(SocialLoginRestrictedException::class)
    fun handleBadRequest(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message = e.message ?: "잘못된 요청입니다.", code = "BAD_REQUEST"))
    }
    @ExceptionHandler(UserNotFoundException::class)
    fun handleNotFound(e: RuntimeException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = e.message ?: "데이터를 찾을 수 없습니다.", code = "NOT_FOUND"))
    }
}
