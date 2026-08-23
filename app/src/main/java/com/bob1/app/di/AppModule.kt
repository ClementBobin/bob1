package com.bob1.app.di

import android.annotation.SuppressLint
import com.bob1.app.data.local.SessionManager
import com.bob1.app.data.repository.*
import com.bob1.app.data.remote.*
import com.bob1.app.domain.repository.*
import com.bob1.app.domain.repository.SeasonPointRepository
import com.bob1.app.mock.registry.buildMockEngine
import com.mirage.bob1.BuildConfig
import dev.kindling.android.natif.BiometricHelper
import dev.kindling.android.natif.NotificationHelper
import dev.kindling.android.natif.VibrationHelper
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

val appModule = module {

    // ── Native helpers ────────────────────────────────────────────────────────
    single { SessionManager(androidContext()) }
    single { NotificationHelper(androidContext()) }
    single { VibrationHelper(androidContext()) }
    single { BiometricHelper(androidContext()) }

    // ── HTTP engine ───────────────────────────────────────────────────────────
    single<HttpClientEngine> {
        when {
            BuildConfig.MOCK_API -> buildMockEngine(delayMs = 400L)
            BuildConfig.DEBUG    -> {
                val tm = trustAllTrustManager()
                val sslContext = SSLContext.getInstance("TLS").apply {
                    init(null, arrayOf(tm), SecureRandom())
                }
                OkHttp.create {
                    preconfigured = OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.socketFactory, tm)
                        .hostnameVerifier { _, _ -> true }
                        .build()
                }
            }
            else -> CIO.create()
        }
    }

    // Bearer token is injected per-request via SessionManager in createHttpClient
    single<HttpClient> {
        createHttpClient(
            baseUrl        = BuildConfig.BASE_URL,
            engine         = get(),
            vibrationHelper = get(),
            sessionManager = get(),
        )
    }

    // ── API clients ───────────────────────────────────────────────────────────
    single { AuthAPI(get()) }
    single { DivisionAPI(get()) }
    single { TeamAPI(get()) }
    single { MatchAPI(get()) }
    single { NotificationAPI(get()) }
    single { PenaltyAPI(get()) }
    single { LocationAPI(get()) }
    single { SeasonPointAPI(get()) }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<AuthRepository>         { AuthRepositoryImpl(get(), get()) }
    single<DivisionRepository>     { DivisionRepositoryImpl(get()) }
    single<TeamRepository>         { TeamRepositoryImpl(get()) }
    single<MatchRepository>        { MatchRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<PenaltyRepository>      { PenaltyRepositoryImpl(get()) }
    single<LocationRepository>     { LocationRepositoryImpl(get()) }
    single<SeasonPointRepository>  { SeasonPointRepositoryImpl(get()) }
}

private fun trustAllTrustManager(): X509TrustManager = @SuppressLint("CustomX509TrustManager")
object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}