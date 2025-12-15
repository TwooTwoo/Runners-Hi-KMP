package good.space.runnershi.user.repository

import good.space.runnershi.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RunnerRepository : JpaRepository<User, Long> {
}