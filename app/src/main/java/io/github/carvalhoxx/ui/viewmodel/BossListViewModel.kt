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

data class BossUiModel(
    val entity: BossEntity,
    val displayElapsedMillis: Long
)

data class BossScreenUiState(
    val game: GameEntity? = null,
    val bosses: List<BossUiModel> = emptyList()
)

class BossListViewModel(
    private val gameId: Long,
    private val repository: BossFightRepository
) : ViewModel() {

    private val bossesFlow = repository.observeBossesByGame(gameId)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val tickerFlow: Flow<Long> = bossesFlow
        .map { list -> list.any { it.isRunning } }
        .distinctUntilChanged()
        .flatMapLatest { anyRunning ->
            if (anyRunning) {
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

    val uiState: StateFlow<BossScreenUiState> = combine(
        repository.observeGameById(gameId),
        bossesFlow,
        tickerFlow
    ) { game, bosses, nowMillis ->
        BossScreenUiState(
            game = game,
            bosses = bosses.map { boss ->
                val currentNow = if (boss.isRunning) maxOf(nowMillis, System.currentTimeMillis()) else nowMillis
                BossUiModel(
                    entity = boss,
                    displayElapsedMillis = BossFightRepository.calculateElapsedMillis(boss, currentNow)
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BossScreenUiState()
    )

    fun addBoss(name: String) {
        viewModelScope.launch {
            repository.createBoss(gameId, name)
        }
    }

    fun deleteBoss(boss: BossEntity) {
        viewModelScope.launch {
            repository.deleteBoss(boss)
        }
    }

    fun incrementDeaths(bossId: Long) {
        viewModelScope.launch {
            repository.incrementDeaths(bossId)
        }
    }

    fun decrementDeaths(bossId: Long) {
        viewModelScope.launch {
            repository.decrementDeaths(bossId)
        }
    }

    fun toggleTimer(bossId: Long) {
        viewModelScope.launch {
            repository.toggleTimer(bossId)
        }
    }

    fun resetTimer(bossId: Long) {
        viewModelScope.launch {
            repository.resetTimer(bossId)
        }
    }

    fun addLap(bossId: Long) {
        viewModelScope.launch {
            repository.addLap(bossId)
        }
    }

    companion object {
        fun provideFactory(
            gameId: Long,
            repository: BossFightRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BossListViewModel(gameId, repository) as T
            }
        }
    }
}
