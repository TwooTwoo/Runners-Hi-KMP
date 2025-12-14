package good.space.runnershi.shared.di

import good.space.runnershi.shared.platform.AndroidLogger
import good.space.runnershi.shared.platform.Logger
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val androidPlatformModule = module {
    single<Logger> { AndroidLogger() }
}
