package good.space.runnershi.user.controller

import good.space.runnershi.model.dto.running.UserHomeResponse
import good.space.runnershi.user.service.UserService
import jakarta.persistence.Id
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/home")
    fun getHomeData(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<UserHomeResponse> {
        val response = userService.loadHomeData(userDetails.userId)
        return ResponseEntity.ok(response)
    }
}
