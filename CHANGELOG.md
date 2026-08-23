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
