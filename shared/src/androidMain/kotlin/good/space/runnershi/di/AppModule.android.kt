package good.space.runnershi.di

import good.space.runnershi.auth.AndroidTokenStorage
import good.space.runnershi.auth.TokenStorage
import good.space.runnershi.database.AppDatabase
import good.space.runnershi.model.domain.AndroidLocationTracker
import good.space.runnershi.model.domain.location.LocationTracker
import good.space.runnershi.repository.AndroidRunningDataSource
import good.space.runnershi.repository.LocalRunningDataSource
import good.space.runnershi.permission.AndroidLocationPermissionManager
import good.space.runnershi.permission.LocationPermissionManager
import good.space.runnershi.service.AndroidServiceLauncher
import good.space.runnershi.service.RunningServiceController
import good.space.runnershi.service.ServiceLauncher
import good.space.runnershi.settings.AndroidSettingsRepository
import good.space.runnershi.settings.SettingsRepository
import good.space.runnershi.util.AndroidNotificationHelper
import good.space.runnershi.util.NotificationHelper
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<TokenStorage> {
        AndroidTokenStorage(context = get())
    }

    single { AppDatabase.getDatabase(get()) }
    single { get<AppDatabase>().runningDao() }

    single<LocationTracker> { AndroidLocationTracker(get()) }
    single<LocalRunningDataSource> { AndroidRunningDataSource(get()) }
    single<NotificationHelper> { AndroidNotificationHelper(get()) }

    single<ServiceLauncher> { AndroidServiceLauncher(get()) }
    
    single<LocationPermissionManager> { AndroidLocationPermissionManager(get()) }

    single {
        RunningServiceController(
            locationTracker = get(),
            runningDataSource = get(),
            settingsRepository = get(),
            notificationHelper = get()
        )
    }

    single<SettingsRepository> {
        AndroidSettingsRepository(context = get())
    }
}
