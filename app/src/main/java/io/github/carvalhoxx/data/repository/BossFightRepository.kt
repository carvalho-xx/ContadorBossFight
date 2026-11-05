package io.github.carvalhoxx.data.repository

import io.github.carvalhoxx.data.local.dao.BossFightDao
import io.github.carvalhoxx.data.local.entity.BossEntity
import io.github.carvalhoxx.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

class BossFightRepository(private val dao: BossFightDao) {

    fun observeAllGames(): Flow<List<GameEntity>> = dao.observeAllGames()

    fun observeGameById(gameId: Long): Flow<GameEntity?> = dao.observeGameById(gameId)

    fun observeBossesByGame(gameId: Long): Flow<List<BossEntity>> = dao.observeBossesByGame(gameId)

    fun observeAllBosses(): Flow<List<BossEntity>> = dao.observeAllBosses()

    fun observeBossById(bossId: Long): Flow<BossEntity?> = dao.observeBossById(bossId)

    suspend fun createGame(name: String): Long {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return -1L
        return dao.insertGame(GameEntity(name = trimmed))
    }

    suspend fun deleteGame(game: GameEntity) {
        dao.deleteGame(game)
    }

    suspend fun createBoss(
        gameId: Long,
        name: String,
        initialDeaths: Int = 0,
        initialDurationMillis: Long = 0L,
        isDefeated: Boolean = false
    ): Long {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return -1L
        return dao.insertBoss(
            BossEntity(
                gameId = gameId,
                name = trimmed,
                deaths = initialDeaths.coerceAtLeast(0),
                accumulatedMillis = initialDurationMillis.coerceAtLeast(0L),
                isDefeated = isDefeated
            )
        )
    }

    suspend fun toggleBossDefeated(bossId: Long, nowEpochMillis: Long = System.currentTimeMillis()) {
        val boss = dao.getBossById(bossId) ?: return
        val newDefeated = !boss.isDefeated
        if (newDefeated && boss.isRunning) {
            val delta = boss.lastStartedEpochMillis?.let { (nowEpochMillis - it).coerceAtLeast(0L) } ?: 0L
            dao.updateBoss(
                boss.copy(
                    isDefeated = true,
                    isRunning = false,
                    accumulatedMillis = boss.accumulatedMillis + delta,
                    lastStartedEpochMillis = null
                )
            )
        } else {
            dao.updateBoss(boss.copy(isDefeated = newDefeated))
        }
    }

    suspend fun deleteBoss(boss: BossEntity) {
        dao.deleteBoss(boss)
    }

    suspend fun incrementDeaths(bossId: Long) {
        val boss = dao.getBossById(bossId) ?: return
        dao.updateBoss(boss.copy(deaths = boss.deaths + 1))
    }

    suspend fun decrementDeaths(bossId: Long) {
        val boss = dao.getBossById(bossId) ?: return
        val newDeaths = (boss.deaths - 1).coerceAtLeast(0)
        dao.updateBoss(boss.copy(deaths = newDeaths))
    }

    suspend fun setDeaths(bossId: Long, deaths: Int) {
        val boss = dao.getBossById(bossId) ?: return
        dao.updateBoss(boss.copy(deaths = deaths.coerceAtLeast(0)))
    }

    suspend fun setTimerDuration(bossId: Long, durationMillis: Long) {
        val boss = dao.getBossById(bossId) ?: return
        dao.updateBoss(
            boss.copy(
                isRunning = false,
                accumulatedMillis = durationMillis.coerceAtLeast(0L),
                lastStartedEpochMillis = null
            )
        )
    }

    suspend fun toggleTimer(bossId: Long, nowEpochMillis: Long = System.currentTimeMillis()) {
        val boss = dao.getBossById(bossId) ?: return
        if (boss.isRunning) {
            val delta = boss.lastStartedEpochMillis?.let { (nowEpochMillis - it).coerceAtLeast(0L) } ?: 0L
            dao.updateBoss(
                boss.copy(
                    isRunning = false,
                    accumulatedMillis = boss.accumulatedMillis + delta,
                    lastStartedEpochMillis = null
                )
            )
        } else {
            dao.updateBoss(
                boss.copy(
                    isRunning = true,
                    lastStartedEpochMillis = nowEpochMillis
                )
            )
        }
    }

    suspend fun resetTimer(bossId: Long) {
        val boss = dao.getBossById(bossId) ?: return
        dao.updateBoss(
            boss.copy(
                isRunning = false,
                accumulatedMillis = 0L,
                lastStartedEpochMillis = null,
                laps = emptyList()
            )
        )
    }

    suspend fun addLap(bossId: Long, nowEpochMillis: Long = System.currentTimeMillis()) {
        val boss = dao.getBossById(bossId) ?: return
        val currentElapsed = calculateElapsedMillis(boss, nowEpochMillis)
        if (currentElapsed <= 0L) return
        dao.updateBoss(
            boss.copy(
                laps = boss.laps + currentElapsed
            )
        )
    }

    companion object {
        fun calculateElapsedMillis(
            boss: BossEntity,
            nowEpochMillis: Long = System.currentTimeMillis()
        ): Long {
            return if (boss.isRunning && boss.lastStartedEpochMillis != null) {
                boss.accumulatedMillis + (nowEpochMillis - boss.lastStartedEpochMillis).coerceAtLeast(0L)
            } else {
                boss.accumulatedMillis
            }
        }
    }
}
