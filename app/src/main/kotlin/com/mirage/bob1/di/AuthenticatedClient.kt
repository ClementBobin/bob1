package com.mirage.bob1.di

import javax.inject.Qualifier

/** The Ktor client that sends the bearer token and refreshes it on 401. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthenticatedClient
