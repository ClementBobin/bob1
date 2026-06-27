package com.bob1.app.di

import android.annotation.SuppressLint
import com.bob1.app.BuildConfig
import com.bob1.app.data.local.SessionManager
import com.bob1.app.data.remote.createHttpClient
import com.bob1.app.data.repository.*
import com.bob1.app.domain.repository.*
import com.bob1.app.mock.registry.buildMockEngine
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

    // ── Native helpers (Kindling) ─────────────────────────────────────────────
    single { SessionManager(androidContext()) }
    single { NotificationHelper(androidContext()) }
    single { VibrationHelper(androidContext()) }

    // ── HTTP engine ───────────────────────────────────────────────────────────
    single<HttpClientEngine> {
        when {
            false -> buildMockEngine(delayMs = 300L)
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

    single<HttpClient> {
        createHttpClient(
            baseUrl        = BuildConfig.BASE_URL,
            engine         = get(),
            sessionManager = get(),
        )
    }

    // ── Repositories ──────────────────────────────────────────────────────────
    single<AuthRepository>         { AuthRepositoryImpl(get(), get()) }
    single<DivisionRepository>     { DivisionRepositoryImpl(get()) }
    single<TeamRepository>         { TeamRepositoryImpl(get()) }
    single<MatchRepository>        { MatchRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<AdminRepository>        { AdminRepositoryImpl(get(), get()) }
}

private fun trustAllTrustManager(): X509TrustManager = @SuppressLint("CustomX509TrustManager")
object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) = Unit
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}