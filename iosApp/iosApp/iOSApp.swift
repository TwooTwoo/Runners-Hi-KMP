import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        // Koin 초기화 - 가이드 IDEA 5
        // iOS 플랫폼 모듈은 shared 모듈에서 import하여 사용
        // initKoin(additionalModules: [iosPlatformModule])
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}