package good.space.runnershi.user.domain

import good.space.runnershi.user.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class LocalUser (
    @Column(nullable = false)
    var password: String,
    name: String,
    email: String
) : User(
    name = name,
    email = email,
    userType = UserType.LOCAL
)