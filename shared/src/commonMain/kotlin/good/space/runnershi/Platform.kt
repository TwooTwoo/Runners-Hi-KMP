package good.space.runnershi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform