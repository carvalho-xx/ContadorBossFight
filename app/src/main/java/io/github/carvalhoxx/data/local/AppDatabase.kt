package io.github.carvalhoxx.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.carvalhoxx.data.local.dao.BossFightDao
import io.github.carvalhoxx.data.local.entity.BossEntity
import io.github.carvalhoxx.data.local.entity.GameEntity

@Database(
    entities = [GameEntity::class, BossEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bossFightDao(): BossFightDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "contador_boss_fight.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
