package io.github.carvalhoxx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.carvalhoxx.data.local.entity.BossEntity
import io.github.carvalhoxx.data.local.entity.GameEntity
import io.github.carvalhoxx.data.repository.BossFightRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

data class BossDetailUiState(
    val game: GameEntity? = null,
    val boss: BossEntity? = null,
    val displayElapsedMillis: Long = 0L
)

class BossDetailViewModel(
    private val gameId: Long,
    private val bossId: Long,
    private val repository: BossFightRepository
) : ViewModel() {

    private val bossFlow = repository.observeBossById(bossId)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val tickerFlow: Flow<Long> = bossFlow
        .map { it?.isRunning == true }
        .distinctUntilChanged()
        .flatMapLatest { isRunning ->
            if (isRunning) {
                flow {
                    while (true) {
                        emit(System.currentTimeMillis())
                        delay(50.milliseconds)
                    }
                }
            } else {
                flowOf(System.currentTimeMillis())
            }
        }

    val uiState: StateFlow<BossDetailUiState> = combine(
        repository.observeGameById(gameId),
        bossFlow,
        tickerFlow
    ) { game, boss, nowMillis ->
        val elapsed = if (boss != null) {
            val currentNow = if (boss.isRunning) maxOf(nowMillis, System.currentTimeMillis()) else nowMillis
            BossFightRepository.calculateElapsedMillis(boss, currentNow)
        } else {
            0L
        }
        BossDetailUiState(
            game = game,
            boss = boss,
            displayElapsedMillis = elapsed
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BossDetailUiState()
    )

    fun incrementDeaths() {
        viewModelScope.launch {
            repository.incrementDeaths(bossId)
        }
    }

    fun decrementDeaths() {
        viewModelScope.launch {
            repository.decrementDeaths(bossId)
        }
    }

    fun toggleTimer() {
        viewModelScope.launch {
            repository.toggleTimer(bossId)
        }
    }

    fun resetTimer() {
        viewModelScope.launch {
            repository.resetTimer(bossId)
        }
    }

    fun addLap() {
        viewModelScope.launch {
            repository.addLap(bossId)
        }
    }

    fun deleteBoss(onDeleted: () -> Unit) {
        val currentBoss = uiState.value.boss ?: return
        viewModelScope.launch {
            repository.deleteBoss(currentBoss)
            onDeleted()
        }
    }

    companion object {
        fun provideFactory(
            gameId: Long,
            bossId: Long,
            repository: BossFightRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BossDetailViewModel(gameId, bossId, repository) as T
            }
        }
    }
}
