package good.space.runnershi.shared.di

import good.space.runnershi.shared.platform.IOSLogger
import good.space.runnershi.shared.platform.Logger
import org.koin.dsl.module

val iosPlatformModule = module {
    single<Logger> { IOSLogger() }
}
