# Kotlin Multiplatform 프로젝트 설정 가이드

## 📋 구현된 주요 기능

### 1. Version Catalog (libs.versions.toml)
- ✅ Koin (DI)
- ✅ Ktor Client (Networking)
- ✅ SQLDelight (Database)
- ✅ Kotlinx Serialization
- ✅ Multiplatform Settings
- ✅ SKIE (iOS 상호운용성)
- ✅ Mokkery (테스트)

### 2. Convention Plugins (build-logic)
- ✅ `AndroidApplicationConventionPlugin`
- ✅ `AndroidLibraryConventionPlugin`
- ✅ `KmpLibraryConventionPlugin`
- ✅ `KmpComposeApplicationConventionPlugin`

### 3. 소스 셋 계층 구조
- ✅ `commonMain` → 공통 코드
- ✅ `jvmMain` → Android + Desktop 공통
- ✅ `iosMain` → iOS 네이티브 공통
- ✅ `androidMain` → Android 전용
- ✅ `iosArm64Main` / `iosSimulatorArm64Main` → iOS 아키텍처별

### 4. 아키텍처 패턴
- ✅ MVI/MVVM 패턴 (`ViewModel` 베이스 클래스)
- ✅ 단방향 데이터 흐름 (StateFlow 기반)
- ✅ Interface 기반 플랫폼 추상화 (expect/actual 대신)

### 5. 표준 라이브러리 스택 (The Holy Trinity)
- ✅ **Networking**: Ktor Client
- ✅ **Database**: SQLDelight
- ✅ **Serialization**: kotlinx.serialization
- ✅ **Settings**: Multiplatform Settings
- ✅ **DI**: Koin

## 🚀 사용 방법

### Android 앱 실행
```bash
./gradlew :composeApp:assembleDebug
```

### iOS 프레임워크 빌드
```bash
./gradlew :shared:linkDebugFrameworkIosArm64
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### 모든 플랫폼 테스트 실행
```bash
./gradlew allTests
```

## 📁 프로젝트 구조

```
RunnersHi/
├── build-logic/              # Convention Plugins
│   └── convention/
│       └── src/main/kotlin/
│           ├── android-application.gradle.kts
│           ├── android-library.gradle.kts
│           ├── kmp-library.gradle.kts
│           └── kmp-compose-application.gradle.kts
├── composeApp/               # Compose Multiplatform 앱
├── shared/                   # 공유 모듈
│   └── src/
│       ├── commonMain/
│       │   ├── kotlin/
│       │   │   ├── architecture/    # ViewModel 등
│       │   │   ├── di/              # Koin 모듈
│       │   │   └── platform/        # Logger 인터페이스
│       │   └── sqldelight/          # SQLDelight 스키마
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── platform/        # AndroidLogger 구현
│       └── iosMain/
│           └── kotlin/
│               └── platform/        # IOSLogger 구현
├── iosApp/                   # iOS 네이티브 앱
└── gradle/
    └── libs.versions.toml    # Version Catalog
```

## 🔧 주요 설정 파일

### Version Catalog
`gradle/libs.versions.toml`에 모든 의존성 버전이 중앙 관리됩니다.

### Convention Plugins
`build-logic/convention/`에서 빌드 로직을 모듈화하여 재사용합니다.

## 📝 다음 단계

1. **Repository 패턴 구현**: `shared/src/commonMain`에 데이터 레이어 추가
2. **UseCase 구현**: 비즈니스 로직 레이어 추가
3. **UI State 정의**: 각 화면별 State 클래스 정의
4. **SKIE 설정 확인**: iOS에서 Kotlin 코드 사용 시 자연스러운 Swift API 생성 확인

## 📚 참고 자료

- [Kotlin Multiplatform 공식 문서](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin 문서](https://insert-koin.io/)
- [Ktor 문서](https://ktor.io/)
- [SQLDelight 문서](https://cashapp.github.io/sqldelight/)

## ⚠️ 주의사항

- SQLDelight 스키마 파일은 `src/commonMain/sqldelight/` 경로에 있어야 합니다.
- iOS 빌드 시 Xcode에서 프레임워크 경로를 올바르게 설정해야 합니다.
- SKIE 플러그인은 iOS 상호운용성을 크게 개선하지만, 빌드 시간이 약간 증가할 수 있습니다.
