package good.space.runnershi.shared.di

fun initKoinIOS() {
    initKoin(extraModules = listOf(iosPlatformModule))
}
