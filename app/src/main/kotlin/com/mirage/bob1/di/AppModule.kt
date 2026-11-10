package com.mirage.bob1.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.mirage.bob1.BuildConfig
import com.mirage.bob1.data.remote.AuthApi
import com.mirage.bob1.data.remote.AuthInterceptor
import com.mirage.bob1.data.remote.Bob1Api
import com.mirage.bob1.data.remote.RemoteConfigBaseUrlInterceptor
import com.mirage.bob1.data.remote.TokenAuthenticator
import com.mirage.bob1.data.repository.*
import com.mirage.bob1.domain.repository.*
import org.bob1.capacity.haptics.Haptics
import org.bob1.capacity.network.NetworkMonitor
import org.bob1.capacity.permission.NotificationPermission
import org.bob1.capacity.push.PushMessaging
import org.bob1.capacity.session.SecureSession
import org.bob1.room.Bob1Database
import org.bob1.room.dao.CommitmentDao
import org.bob1.room.dao.MatchDao
import org.bob1.room.dao.NotificationDao
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AnonymousClient
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class AuthenticatedClient

/** Template `kotlin_app-module`: everything the app needs wired once, at the singleton level. */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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

    // ── Network ─────────────────────────────────────────────
    @Provides @Singleton fun json(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false      // omit null query criteria instead of sending "x": null
        coerceInputValues = true
    }

    private fun logging() = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        redactHeader("Authorization")
    }

    @Provides @Singleton @AnonymousClient
    fun anonymousClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(RemoteConfigBaseUrlInterceptor())
        .addInterceptor(logging())
        .build()

    @Provides @Singleton
    fun authApi(@AnonymousClient client: OkHttpClient, json: Json): AuthApi = retrofit(client, json).create(AuthApi::class.java)

    @Provides @Singleton @AuthenticatedClient
    fun authenticatedClient(session: SecureSession, authApi: AuthApi): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(session))
        .authenticator(TokenAuthenticator(session, authApi))
        .addInterceptor(RemoteConfigBaseUrlInterceptor())
        .addInterceptor(logging())
        .build()

    @Provides @Singleton
    fun bob1Api(@AuthenticatedClient client: OkHttpClient, json: Json): Bob1Api = retrofit(client, json).create(Bob1Api::class.java)

    private fun retrofit(client: OkHttpClient, json: Json) = Retrofit.Builder()
        // Base URL is rewritten at runtime by RemoteConfigBaseUrlInterceptor.
        .baseUrl("https://remote-config.invalid/")
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun auth(impl: AuthRepositoryImpl): AuthRepository
    @Binds abstract fun matches(impl: MatchRepositoryImpl): MatchRepository
    @Binds abstract fun carpool(impl: CarpoolRepositoryImpl): CarpoolRepository
    @Binds abstract fun club(impl: ClubRepositoryImpl): ClubRepository
    @Binds abstract fun family(impl: FamilyRepositoryImpl): FamilyRepository
    @Binds abstract fun commitments(impl: CommitmentRepositoryImpl): CommitmentRepository
    @Binds abstract fun ranking(impl: RankingRepositoryImpl): RankingRepository
    @Binds abstract fun notifications(impl: NotificationRepositoryImpl): NotificationRepository
    @Binds abstract fun push(impl: PushRepositoryImpl): PushRepository
    @Binds abstract fun staff(impl: StaffRepositoryImpl): StaffRepository
}
