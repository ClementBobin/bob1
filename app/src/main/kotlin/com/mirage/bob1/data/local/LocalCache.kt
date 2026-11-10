package com.mirage.bob1.data.local

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bob1.room.Bob1Database

/** Wipes every cached table on sign-out. */
@Singleton
class LocalCache @Inject constructor(private val db: Bob1Database) {
    suspend fun clear() = withContext(Dispatchers.IO) { db.clearAllTables() }
}
