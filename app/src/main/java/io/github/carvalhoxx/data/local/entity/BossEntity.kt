package io.github.carvalhoxx.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bosses",
    foreignKeys = [
        ForeignKey(
            entity = GameEntity::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["gameId"])]
)
data class BossEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val name: String,
    val deaths: Int = 0,
    val isRunning: Boolean = false,
    val accumulatedMillis: Long = 0L,
    val lastStartedEpochMillis: Long? = null,
    val laps: List<Long> = emptyList(),
    val isDefeated: Boolean = false
)
