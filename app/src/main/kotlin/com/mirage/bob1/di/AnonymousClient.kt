package com.mirage.bob1.di

import javax.inject.Qualifier

/** The Ktor client without a session: login, register, refresh. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AnonymousClient
