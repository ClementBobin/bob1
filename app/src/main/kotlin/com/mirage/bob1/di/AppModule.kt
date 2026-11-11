package com.mirage.bob1.di

import android.content.Context
import com.mirage.bob1.BuildConfig
import com.mirage.bob1.data.remote.ApiUrlHolder
import com.mirage.bob1.data.remote.AuthAPI
import com.mirage.bob1.data.remote.createHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.kindling.android.natif.VibrationHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.logging.LogLevel
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import org.bob1.capacity.haptics.Haptics
import org.bob1.capacity.network.NetworkMonitor
import org.bob1.capacity.permission.NotificationPermission
import org.bob1.capacity.push.PushMessaging
import org.bob1.capacity.session.SecureSession
import org.bob1.room.Bob1Database
import org.bob1.room.dao.CommitmentDao
import org.bob1.room.dao.MatchDao
import org.bob1.room.dao.NotificationDao

/** Template `kotlin_app-module`: everything the app needs wired once, at the singleton level. */
@Module
@InstallIn(SingletonComponent::class)
internal object AppModule {

    // ── Room (external module :room) ────────────────────────
    @Provides @Singleton fun database(@ApplicationContext c: Context): Bob1Database = Bob1Database.build(c)
    @Provides fun matchDao(db: Bob1Database): MatchDao = db.matchDao()
    @Provides fun commitmentDao(db: Bob1Database): CommitmentDao = db.commitmentDao()
    @Provides fun notificationDao(db: Bob1Database): NotificationDao = db.notificationDao()

    // ── Native capacity (external module :capacity, built on Kindling :android) ─
    @Provides @Singleton fun secureSession(@ApplicationContext c: Context) = SecureSession(c)
    @Provides @Singleton fun networkMonitor(@ApplicationContext c: Context) = NetworkMonitor(c)
    @Provides @Singleton fun notificationPermission(@ApplicationContext c: Context) = NotificationPermission(c)
    @Provides @Singleton fun pushMessaging() = PushMessaging()
    @Provides @Singleton fun haptics() = Haptics()
    @Provides @Singleton fun vibrationHelper(@ApplicationContext c: Context) = VibrationHelper(c)

    // ── Network (Ktor) ──────────────────────────────────────
    @Provides @Singleton fun json(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false      // omit null query criteria instead of sending "x": null
        coerceInputValues = true
    }

    private val logLevel get() = if (BuildConfig.DEBUG) LogLevel.INFO else LogLevel.NONE

    @Provides @Singleton @AnonymousClient
    fun anonymousClient(json: Json, vibration: VibrationHelper): HttpClient =
        createHttpClient({ ApiUrlHolder.current }, json, vibrationHelper = vibration, logLevel = logLevel)

    @Provides @Singleton @AuthenticatedClient
    fun authenticatedClient(
        json: Json,
        vibration: VibrationHelper,
        session: SecureSession,
        @AnonymousClient anonymous: HttpClient,
    ): HttpClient = createHttpClient(
        { ApiUrlHolder.current }, json,
        vibrationHelper = vibration,
        sessionManager = session,
        refreshSession = { refreshToken -> AuthAPI(anonymous).refresh(refreshToken) },
        logLevel = logLevel,
    )
}
