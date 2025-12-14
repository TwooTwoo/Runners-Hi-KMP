package good.space.runnershi.shared.di

import org.koin.core.Koin
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.koinApplication

fun initKoin(
    appDeclaration: KoinAppDeclaration = {},
    extraModules: List<Module> = emptyList()
): Koin {
    return startKoin {
        appDeclaration()
        modules(
            appModule,
            *extraModules.toTypedArray()
        )
    }.koin
}
