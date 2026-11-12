# BOB1 mobile

Android client for [Bob1-api-2](https://github.com/ClementBobin/Bob1-api-2). Kotlin · Jetpack Compose · MVVM · Hilt · Ktor · Room, with UI/navigation/native capabilities from [Kindling](https://clementbobin.github.io/Kindling/llms.txt).

## Layout

```
:app        com.mirage.bob1
  data/     dto · local (Room -> domain mapping) · remote (Ktor client + one *API class per feature) · repository
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

- Lists use the HTTP `QUERY` method (`client.query(path, body)` in `HttpHelpers.kt`), as the API does.
- Calendar, commitments and notifications are offline-first (Room is the source of truth, refresh writes into it).
- `createHttpClient` (data/remote) owns the Ktor client: bearer token, one refresh-and-replay on 401, a rejected refresh token clears the session (back to login),
  and every HTTP/network failure becomes an `HttpException` with a toast + vibration (de-duplicated over 4 s). ViewModels don't toast `HttpException` again.
- One `*API` class per feature (`MatchAPI`, `NotificationAPI`...) takes the client; repositories call them through `apiCall { }` (which keeps coroutine cancellation intact, unlike `runCatching`).
- `AuthAPI` uses the anonymous client, every other API the authenticated one (`@AnonymousClient` / `@AuthenticatedClient`).
- Request paths are relative (`"matches"`), the base URL (`API_BASE_URL`, ends with `/api/v1/`) is added by the client.
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

## Seasons

A season runs 1 Sept to 31 Aug (label `2026-2027`); `QUERY /seasons` lists them and the API marks the current one.
The ranking screens (people / families, and the officials' ranking with the history of my points) have a season selector; no season means the current one.
Past seasons are read-only on the API side (`season.closed`), and a parent can only confirm presence / sign up for a match when one of their players is in one of its teams (`participation.not_in_team`).
Manual point adjustments are tagged "Manuel", and a revoked one "Révoqué", in the points history.
