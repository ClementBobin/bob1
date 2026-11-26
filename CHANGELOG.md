## [1.15.0](https://github.com/ClementBobin/bob1/compare/v1.14.0...v1.15.0) (2026-11-26)

### Features

* register as parent or staff with optional family (code ([02067a2](https://github.com/ClementBobin/bob1/commit/02067a2afcd3c695c4dddf2c7f6dce7c1495620f))

## [1.14.0](https://github.com/ClementBobin/bob1/compare/v1.13.0...v1.14.0) (2026-11-24)

### Features

* play integrity ([9fb36d8](https://github.com/ClementBobin/bob1/commit/9fb36d8bc15103dfafbf649d56f7743cc128f44f))

## [1.13.0](https://github.com/ClementBobin/bob1/compare/v1.12.0...v1.13.0) (2026-11-24)

### Features

* added biométrie ([6d69e76](https://github.com/ClementBobin/bob1/commit/6d69e76fe58c3528c80225afd045ca5e56b98531))

## [1.12.0](https://github.com/ClementBobin/bob1/compare/v1.11.0...v1.12.0) (2026-11-16)

### Features

* added failsafe firebase missing ([1edd37d](https://github.com/ClementBobin/bob1/commit/1edd37d5a739159a67e14ec5f02caaf7dcad2e5c))

### Bug Fixes

* ci/cd format ([b14743f](https://github.com/ClementBobin/bob1/commit/b14743fa1fbf7a9c9c542a6117de131e27c3ee17))

## [1.11.0](https://github.com/ClementBobin/bob1/compare/v1.10.0...v1.11.0) (2026-11-15)

### Features

* add .releaserc.json and CHANGELOG.md for semantic release configuration ([7b238fe](https://github.com/ClementBobin/bob1/commit/7b238fe97384ea542ff54fa1bea112c99b2d021c))
* add adaptive icons and update drawable resources for improved app visuals ([7f404e1](https://github.com/ClementBobin/bob1/commit/7f404e1efde99a4eac784b4fde172a5fe9ecfdcd))
* add dependency injection to RemoteConfig class constructor ([cde545c](https://github.com/ClementBobin/bob1/commit/cde545cae994e2684a362c5e9ecfd8e6fbd0505e))
* add Firebase Crashlytics and Performance Monitoring plugins and dependencies ([f1254b4](https://github.com/ClementBobin/bob1/commit/f1254b473d011ce404a4d6125e364341a74fd306))
* add Gradle startup scripts for POSIX and Windows environments ([a64cefe](https://github.com/ClementBobin/bob1/commit/a64cefe1d606e49bb0707a0c42dd171f0fc2085a))
* add Gradle wrapper files for project setup ([2be9387](https://github.com/ClementBobin/bob1/commit/2be93872b64c3af55e6043e7889df7d0ccbdfd27))
* add manual workflow for building and uploading Android APK ([91afb4b](https://github.com/ClementBobin/bob1/commit/91afb4bb461c8c19de7d9f1e80b504559b367c80))
* ci/cd ([73f0845](https://github.com/ClementBobin/bob1/commit/73f0845c9bd7417bf70a7587bfe3aa04f5d7c896))
* ci/cd ([9b58dc9](https://github.com/ClementBobin/bob1/commit/9b58dc9b8d5e46b6ed74a73a4ebc5ea35e222bd2))
* implement Firebase Remote Config for dynamic API URL management ([36bda73](https://github.com/ClementBobin/bob1/commit/36bda73faff8c1359e2d670ec547a71f079781e1))
* implement Ktor client with authenticated and anonymous API classes for improved network handling ([c6c3d3e](https://github.com/ClementBobin/bob1/commit/c6c3d3ebee7f3ea5aa994f7f647c5b57b4b8be21))
* implement season management with new SeasonDto and related updates across the application ([a4ecc3a](https://github.com/ClementBobin/bob1/commit/a4ecc3a50d0b90538f64cafac2511f30674eb651))
* update API base URL handling to use ApiUrlHolder for dynamic configuration ([6cb2cc6](https://github.com/ClementBobin/bob1/commit/6cb2cc66e26364713b39dbb1296c7166e2c94665))
* update API base URL handling to use ApiUrlHolder for dynamic configuration ([61e6101](https://github.com/ClementBobin/bob1/commit/61e610129f9e68988b497a1e1ff5000e55996e0c))
* update API base URL handling to use ApiUrlHolder for dynamic configuration ([f9a0f82](https://github.com/ClementBobin/bob1/commit/f9a0f820a8862d13ec5b59a2eab04d7876e10e6f))
* update build.gradle.kts to support CI versioning and improve signing configuration ([65e225c](https://github.com/ClementBobin/bob1/commit/65e225cd42fd31277389f03a28196d53fa8b8580))

### Bug Fixes

* add missing import for BuildConfig in AppModule.kt ([6cabf3e](https://github.com/ClementBobin/bob1/commit/6cabf3e9e2126e6927c7209084265fdb91327f9d))
* correct Kindling library module paths in libs.versions.toml ([edbef43](https://github.com/ClementBobin/bob1/commit/edbef43c83c2ee84e0ad419b0e5fb112f282f7b1))
* correct package declaration in SlotDto.kt ([2cec400](https://github.com/ClementBobin/bob1/commit/2cec400d535a60f61a353b7963c6720428f2de68))
* update Android SDK setup to skip obsolete tools installation ([4417788](https://github.com/ClementBobin/bob1/commit/4417788c7ecec48275b264ce03a3b80b53b87955))
* update API_BASE_URL and correct Kindling library module paths ([3f45232](https://github.com/ClementBobin/bob1/commit/3f452321fb2c81757987e9bd5d4fc446a620fc4a))
* update CI version name to 'dev' and upgrade firebase-perf-plugin to version 2.0.2 ([bc4b782](https://github.com/ClementBobin/bob1/commit/bc4b7822b62d0e2758e0d97d227909d87d8d0455))
* update compileSdk and targetSdk to 37, enable core library desugaring, and update AGP and Hilt versions ([32552dd](https://github.com/ClementBobin/bob1/commit/32552ddc5674f6dbd16710ccd0043485a08a1dee))
* update firebase-config module to use the correct dependency ([e35324b](https://github.com/ClementBobin/bob1/commit/e35324b0aa0dab08beff1fc21799d46d17044bee))
* update Kindling library group paths in libs.versions.toml ([da97f3a](https://github.com/ClementBobin/bob1/commit/da97f3ad7191323f88bf5a78db985474568cdade))
* update Kindling library group paths in libs.versions.toml ([4460881](https://github.com/ClementBobin/bob1/commit/446088198c1935852fedc29a45179c013ef2b5b8))
* update Kindling library version and module paths in libs.versions.toml ([90c3b40](https://github.com/ClementBobin/bob1/commit/90c3b4048d1ca5e06c18948d2dea9dfc60d3d298))
* update package paths to correct structure across multiple DTO files ([58ffb93](https://github.com/ClementBobin/bob1/commit/58ffb937121016b0effd930ad83005faf80a67f2))

# [1.10.0](https://github.com/ClementBobin/bob1/compare/v1.9.0...v1.10.0) (2026-08-25)

### Bug Fixes

* updated icon launcher ([9665281](https://github.com/ClementBobin/bob1/commit/9665281778e7262124ec43752f1add818c52f77f))

### Features

* add various icon assets for iOS and web ([95bdbca](https://github.com/ClementBobin/bob1/commit/95bdbca0e2e583191f68747fecb190227d6ff729))
* update getCurrentUser method to require token and refactor session handling ([0215a9a](https://github.com/ClementBobin/bob1/commit/0215a9a89350db410e617208dd8badbdcdf32605))

# [1.9.0](https://github.com/ClementBobin/bob1/compare/v1.8.1...v1.9.0) (2026-08-25)

### Features

* add calendar and notifications screens with view models ([e09fca1](https://github.com/ClementBobin/bob1/commit/e09fca16a69d2e9572a63f450ecf7cb475625629))

## [1.8.1](https://github.com/ClementBobin/bob1/compare/v1.8.0...v1.8.1) (2026-08-25)

### Bug Fixes

* Update BASE_URL in build.gradle.kts ([505d8d9](https://github.com/ClementBobin/bob1/commit/505d8d95540d20fdef32dbf9d77919c4fd0e9fe7))

# [1.8.0](https://github.com/ClementBobin/bob1/compare/v1.7.1...v1.8.0) (2026-08-23)

### Bug Fixes

* Change application namespace and ID to com.mirage.bob1 ([d18026b](https://github.com/ClementBobin/bob1/commit/d18026b32d5879d2deb9ff20501664d14aaedd29))

### Features

* update BuildConfig references and improve login handling in AuthRepositoryImpl ([3df7ede](https://github.com/ClementBobin/bob1/commit/3df7ede82fbbd4cc306614783a50b15969b9ab8c))
* Update Play Store publish step with new package name ([ca79c38](https://github.com/ClementBobin/bob1/commit/ca79c383faa68af80035690bda2699e025211c57))

## [1.7.1](https://github.com/ClementBobin/bob1/compare/v1.7.0...v1.7.1) (2026-08-20)

### Bug Fixes

* Update README with project documentation details ([36fa3c0](https://github.com/ClementBobin/bob1/commit/36fa3c0183ad46d4f65df52e3ad013b82451c524))

# [1.7.0](https://github.com/ClementBobin/bob1/compare/v1.6.1...v1.7.0) (2026-08-20)

### Features

* enhance biometric authentication flow with token management and loading states ([0206ca7](https://github.com/ClementBobin/bob1/commit/0206ca72ba1b28e2ce596fac24d4de055174eb11))
* implement biometric authentication flow with token management ([f62cc20](https://github.com/ClementBobin/bob1/commit/f62cc2055137edb4e2b62fa0383b7e1dc1daa2bc))

## [1.6.1](https://github.com/ClementBobin/bob1/compare/v1.6.0...v1.6.1) (2026-08-20)

### Bug Fixes

* formatting in README.md ([99ba638](https://github.com/ClementBobin/bob1/commit/99ba638364dfd365d61609e9dfce7b4ec405bc6b))

# [1.6.0](https://github.com/ClementBobin/bob1/compare/v1.5.0...v1.6.0) (2026-08-20)

### Features

* enhance biometric credential management during login and registration ([48bbe7a](https://github.com/ClementBobin/bob1/commit/48bbe7a2f0fcf041dcca63c2b678fbd3731c14e4))
* implement biometric authentication for login and credential management ([add4587](https://github.com/ClementBobin/bob1/commit/add45876d215823a68936cf148aed8657d2fe05b))

# [1.5.0](https://github.com/ClementBobin/bob1/compare/v1.4.0...v1.5.0) (2026-08-19)

### Bug Fixes

* add debug output for build artifacts and improve artifact upload conditions ([e3cbde1](https://github.com/ClementBobin/bob1/commit/e3cbde1f2305118387b1c3905d1db2850f867b01))
* enable core library desugaring and add dependency for desugar_jdk_libs ([ca399cb](https://github.com/ClementBobin/bob1/commit/ca399cb8c062ebf80f6ac1ac9459ba334694832a))
* remove redundant 'needs' declaration in build-and-release job ([99711a2](https://github.com/ClementBobin/bob1/commit/99711a2abb2e4715b547806d8cac47621fbb781a))
* revert JDK version to 17 and clean up build steps in publish workflow ([407aab4](https://github.com/ClementBobin/bob1/commit/407aab418da9ae4a7058ceea9f6666b07c8e8ca4))
* update .releaserc.json to use environment variables for APK and AAB paths ([8b44d60](https://github.com/ClementBobin/bob1/commit/8b44d60e3931e5c8ca75d53afdf098f6f9782f4e))
* update AAB path environment variable in publish workflow ([fc2c744](https://github.com/ClementBobin/bob1/commit/fc2c74429c45910fcca424d1a50471d2288bf627))
* update imports and ProGuard rules for Koin and Kindling components ([79f8a4e](https://github.com/ClementBobin/bob1/commit/79f8a4ee1def4e02135c9b1f803f50c7bce6d884))
* update semantic-release plugins to use angular preset ([55f71de](https://github.com/ClementBobin/bob1/commit/55f71dec3859524a7619549733ab0a8fc9963c35))

### Features

* enhance build workflow with semantic versioning and artifact handling for APK and AAB ([59806d4](https://github.com/ClementBobin/bob1/commit/59806d402f70fd77c79fcf12b08f131d1c01318d))
* enhance CI versioning by injecting versionName and versionCode from build parameters ([2bb9b4b](https://github.com/ClementBobin/bob1/commit/2bb9b4beb99c6e51c7d6531a4a5cadea6e1a4344))
* implement SeasonPoint feature with API integration and update user roles to integer representation ([3948555](https://github.com/ClementBobin/bob1/commit/3948555f5b61933ea808a191820d5a321fcd3d44))
* update Match and Notification DTOs to handle integer-based API responses, enhance mock data, and improve UI handling for match details ([b99fee3](https://github.com/ClementBobin/bob1/commit/b99fee38fb97033883b4a00579658abc9b37480d))

# [1.4.0](https://github.com/ClementBobin/bob1/compare/v1.3.0...v1.4.0) (2026-08-18)

### Features

* remove androidx.compose.bom dependency from build.gradle.kts ([784803a](https://github.com/ClementBobin/bob1/commit/784803acfd4622f9bc4f7544d050d1cc86a05fd3))
* update BASE_URL in build.gradle.kts and remove minification for release build ([3c4edc4](https://github.com/ClementBobin/bob1/commit/3c4edc4e7410a3fb5bf56bdd07cc8b72f81ece20))

# [1.3.0](https://github.com/ClementBobin/bob1/compare/v1.2.0...v1.3.0) (2026-08-18)

### Features

* improve signing configuration handling in build.gradle.kts ([6897e80](https://github.com/ClementBobin/bob1/commit/6897e803073e112c52eb918cc5b062898ded16ee))
* update build workflow to support APK and AAB signing, improve artifact handling ([80b5041](https://github.com/ClementBobin/bob1/commit/80b5041af95c500f455d6b6523fe71e544fc5e22))

# [1.2.0](https://github.com/ClementBobin/bob1/compare/v1.1.0...v1.2.0) (2026-08-18)

### Features

* implement signing configuration for release APK and update build workflow ([4a25e08](https://github.com/ClementBobin/bob1/commit/4a25e08fd4fa237a50cee84e3a802095ed351982))

# [1.1.0](https://github.com/ClementBobin/bob1/compare/v1.0.0...v1.1.0) (2026-07-14)

### Features

* enhance notification and location handling, add new APIs and update repositories ([3611cf0](https://github.com/ClementBobin/bob1/commit/3611cf08d9f98e6e9f20afe8be85c8c06483583c))

# 1.0.0 (2026-07-06)

### Features

* fix viewmodel ([fef5566](https://github.com/ClementBobin/bob1/commit/fef5566a1059b1e27e36bb0d16e170bfcfa49b25))
* fix viewmodel ([a1c98da](https://github.com/ClementBobin/bob1/commit/a1c98daf9c5503506cc0a5443197c3ca3c44ef0e))
* init structure ([b18562d](https://github.com/ClementBobin/bob1/commit/b18562de87811769b5873489a4e9f53e4a4ffa79))
