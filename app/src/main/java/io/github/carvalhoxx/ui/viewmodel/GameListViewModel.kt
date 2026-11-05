package io.github.carvalhoxx.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import io.github.carvalhoxx.data.local.entity.GameEntity
import io.github.carvalhoxx.data.repository.BossFightRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class GameSummaryUiModel(
    val game: GameEntity,
    val bossCount: Int,
    val totalDeaths: Int
)

class GameListViewModel(
    private val repository: BossFightRepository
) : ViewModel() {

    val gameSummaries: StateFlow<List<GameSummaryUiModel>> = combine(
        repository.observeAllGames(),
        repository.observeAllBosses()
    ) { games, allBosses ->
        val bossesByGame = allBosses.groupBy { it.gameId }
        games.map { game ->
            val gameBosses = bossesByGame[game.id].orEmpty()
            GameSummaryUiModel(
                game = game,
                bossCount = gameBosses.size,
                totalDeaths = gameBosses.sumOf { it.deaths }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun addGame(name: String) {
        viewModelScope.launch {
            repository.createGame(name)
        }
    }

    fun deleteGame(game: GameEntity) {
        viewModelScope.launch {
            repository.deleteGame(game)
        }
    }

    companion object {
        fun provideFactory(repository: BossFightRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return GameListViewModel(repository) as T
                }
            }
    }
}
