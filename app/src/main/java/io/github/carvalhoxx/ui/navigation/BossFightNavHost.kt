package io.github.carvalhoxx.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import io.github.carvalhoxx.data.repository.BossFightRepository
import io.github.carvalhoxx.ui.screen.BossDetailScreen
import io.github.carvalhoxx.ui.screen.BossListScreen
import io.github.carvalhoxx.ui.screen.GameListScreen
import io.github.carvalhoxx.ui.theme.ContadorBossFightTheme
import io.github.carvalhoxx.ui.viewmodel.BossDetailViewModel
import io.github.carvalhoxx.ui.viewmodel.BossListViewModel
import io.github.carvalhoxx.ui.viewmodel.GameListViewModel

@Composable
fun BossFightNavHost(repository: BossFightRepository) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "games"
    ) {
        composable("games") {
            val gameListViewModel: GameListViewModel = viewModel(
                factory = GameListViewModel.provideFactory(repository)
            )
            ContadorBossFightTheme(currentGameName = null) {
                GameListScreen(
                    viewModel = gameListViewModel,
                    onGameClick = { game ->
                        navController.navigate("bosses/${game.id}") {
                            launchSingleTop = true
                        }
                    }
                )
            }
        }

        composable(
            route = "bosses/{gameId}",
            arguments = listOf(navArgument("gameId") { type = NavType.LongType })
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            val bossListViewModel: BossListViewModel = viewModel(
                key = "boss_list_$gameId",
                factory = BossListViewModel.provideFactory(gameId, repository)
            )
            BossListScreen(
                viewModel = bossListViewModel,
                onBossClick = { bossId ->
                    navController.navigate("boss_detail/$gameId/$bossId") {
                        launchSingleTop = true
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "boss_detail/{gameId}/{bossId}",
            arguments = listOf(
                navArgument("gameId") { type = NavType.LongType },
                navArgument("bossId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val gameId = backStackEntry.arguments?.getLong("gameId") ?: return@composable
            val bossId = backStackEntry.arguments?.getLong("bossId") ?: return@composable
            val bossDetailViewModel: BossDetailViewModel = viewModel(
                key = "boss_detail_${gameId}_$bossId",
                factory = BossDetailViewModel.provideFactory(gameId, bossId, repository)
            )
            BossDetailScreen(
                viewModel = bossDetailViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
