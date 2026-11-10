# BOB1 mobile

Android client for [Bob1-api-2](https://github.com/ClementBobin/Bob1-api-2). Kotlin · Jetpack Compose · MVVM · Hilt · Retrofit · Room, with UI/navigation/native capabilities from [Kindling](https://clementbobin.github.io/Kindling/llms.txt).

## Layout

```
:app        org.bob1.app
  data/     dto · local (Room -> domain mapping) · remote (Retrofit, auth) · repository
  di/       AppModule.kt   (kotlin_app-module: Room, capacity, network, repository bindings)
  domain/   model · repository (interfaces) · usecase
  ui/       core (theme, components, navigation, Bob1ViewModel, AppMode)  ·  screens:
              parent  login, calendar, match, commitments, ranking, notifications, profile
              staff   calendar of open officials' slots, match (sign up / leave), assignments, staff ranking + my points history
              auth    login, registration (new family, or join one with an invitation code)
:room       external module: Bob1Database, entities, DAOs (knows nothing about the app)
:capacity   external module: native capabilities on Kindling :android
            SecureSession · NetworkMonitor · FCM (Bob1MessagingService/PushBridge/PushChannels) · Haptics · NotificationPermission · Directions/ShareText
```

Conventions: one class per file in `dto`, `local`, `remote`, `repository`, `domain/*`.
There is no mapper file: each DTO carries its own `toDomain()` (and `fromDomain(...)` when the app sends it back, e.g. `UserDto`, `RegisterRequestDto`),
`CommitmentType.fromApi(...)` maps the API string, and the Room entity <-> domain mapping stays private inside each `*LocalDataSource`.

Dependencies point one way: `:app -> :room`, `:app -> :capacity`. `:capacity` reaches the app only through `PushBridge` callbacks.

## Run

1. Start the API (default `http://localhost:5000`). The debug build points at `http://10.0.2.2:5000/api/v1/` (emulator -> host).
   Other host: `./gradlew :app:installDebug -Pbob1.apiUrl=http://192.168.1.20:5000/api/v1/` (trailing slash required).
2. Push (optional): put your Firebase `google-services.json` in `app/`. Without it the app runs, it just has no FCM token.
3. Sign in with an account seeded by the API.

## Notes

- Lists use the HTTP `QUERY` method (`@HTTP(method = "QUERY", hasBody = true)`), as the API does.
- Calendar, commitments and notifications are offline-first (Room is the source of truth, refresh writes into it).
- 401 -> `TokenAuthenticator` refreshes once and replays; a rejected refresh token signs the user out.
- Dates without an offset from the API are read as UTC (see `parseUtc`).

## Roles

`GET /me` roles decide the bottom bar (`tabsFor`): Parent, Staff, or both (switch in Profil -> "Mon espace").
Administration (people, matches, slots, penalties, broadcasts) is web-only; an admin-only account just gets the profile tab on mobile.

## Registration

`POST /auth/register`: with `invitationCode` (8 characters, valid 7 days, created by "Inviter un parent") the new parent joins that family; without it a new family is created (`familyName`, or the last name).
The response carries the tokens, so the user is signed in immediately.

## Notifications

Firebase only, no local notifications. The API sends notification + data payloads: when the app is in the background FCM shows them (channel `bob1_push` and icon from the app manifest).
While the app is open nothing pops up; the in-app list is refreshed and the bell badge updates.
