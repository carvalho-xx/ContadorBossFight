package io.github.carvalhoxx.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.carvalhoxx.data.local.entity.BossEntity
import io.github.carvalhoxx.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BossFightDao {
    @Query("SELECT * FROM games ORDER BY name ASC")
    fun observeAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :gameId LIMIT 1")
    fun observeGameById(gameId: Long): Flow<GameEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("SELECT * FROM bosses WHERE gameId = :gameId ORDER BY id ASC")
    fun observeBossesByGame(gameId: Long): Flow<List<BossEntity>>

    @Query("SELECT * FROM bosses ORDER BY id ASC")
    fun observeAllBosses(): Flow<List<BossEntity>>

    @Query("SELECT * FROM bosses WHERE id = :bossId LIMIT 1")
    fun observeBossById(bossId: Long): Flow<BossEntity?>

    @Query("SELECT * FROM bosses WHERE id = :bossId LIMIT 1")
    suspend fun getBossById(bossId: Long): BossEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoss(boss: BossEntity): Long

    @Update
    suspend fun updateBoss(boss: BossEntity)

    @Delete
    suspend fun deleteBoss(boss: BossEntity)
}
