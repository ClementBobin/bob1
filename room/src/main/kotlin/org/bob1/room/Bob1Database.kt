package org.bob1.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.bob1.room.dao.CommitmentDao
import org.bob1.room.dao.MatchDao
import org.bob1.room.dao.NotificationDao
import org.bob1.room.entity.CommitmentEntity
import org.bob1.room.entity.MatchEntity
import org.bob1.room.entity.NotificationEntity

@Database(
    entities = [MatchEntity::class, CommitmentEntity::class, NotificationEntity::class],
    version = 2,   // 2: matches lost divisionId / divisionName (the cache is rebuilt from the API)
    exportSchema = true,
)
abstract class Bob1Database : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun commitmentDao(): CommitmentDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val NAME = "bob1.db"

        /** The only place that knows how the database is built; the app just calls this from its DI module. */
        fun build(context: Context): Bob1Database =
            Room.databaseBuilder(context.applicationContext, Bob1Database::class.java, NAME)
                // The cache is rebuilt from the API, so a destructive migration is safe here.
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
