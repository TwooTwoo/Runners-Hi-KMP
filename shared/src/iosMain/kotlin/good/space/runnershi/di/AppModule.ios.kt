package good.space.runnershi.di

import good.space.runnershi.permission.IosLocationPermissionManager
import good.space.runnershi.permission.LocationPermissionManager
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<LocationPermissionManager> { IosLocationPermissionManager() }
    // TODO: iOS용 구현체 구현
    // single<TokenStorage> { IosTokenStorage() } 
}
