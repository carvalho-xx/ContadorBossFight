package io.github.carvalhoxx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import io.github.carvalhoxx.data.local.AppDatabase
import io.github.carvalhoxx.data.repository.BossFightRepository
import io.github.carvalhoxx.ui.navigation.BossFightNavHost
import io.github.carvalhoxx.ui.theme.ContadorBossFightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getInstance(applicationContext)
        val repository = BossFightRepository(database.bossFightDao())

        setContent {
            val rememberedRepository = remember { repository }
            ContadorBossFightTheme {
                BossFightNavHost(repository = rememberedRepository)
            }
        }
    }
}
