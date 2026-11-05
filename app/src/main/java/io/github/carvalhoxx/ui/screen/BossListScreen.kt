package io.github.carvalhoxx.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.carvalhoxx.ui.util.formatDurationMillis
import io.github.carvalhoxx.ui.viewmodel.BossListViewModel
import io.github.carvalhoxx.ui.viewmodel.BossUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BossListScreen(
    viewModel: BossListViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddBossDialog by rememberSaveable { mutableStateOf(false) }
    var newBossName by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.game?.name ?: "Chefes") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("< Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddBossDialog = true }) {
                Text("+ Boss", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        if (uiState.bosses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum Boss cadastrado neste jogo.\nToque em \"+ Boss\" para adicionar (ex: Godrick).",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.bosses, key = { it.entity.id }) { bossModel ->
                    BossCard(
                        bossModel = bossModel,
                        onIncrementDeath = { viewModel.incrementDeaths(bossModel.entity.id) },
                        onDecrementDeath = { viewModel.decrementDeaths(bossModel.entity.id) },
                        onToggleTimer = { viewModel.toggleTimer(bossModel.entity.id) },
                        onResetTimer = { viewModel.resetTimer(bossModel.entity.id) },
                        onAddLap = { viewModel.addLap(bossModel.entity.id) },
                        onDeleteBoss = { viewModel.deleteBoss(bossModel.entity) }
                    )
                }
            }
        }

        if (showAddBossDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddBossDialog = false
                    newBossName = ""
                },
                title = { Text("Novo Boss") },
                text = {
                    Column {
                        Text("Informe o nome do chefe:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newBossName,
                            onValueChange = { newBossName = it },
                            placeholder = { Text("Ex: Godrick") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newBossName.isNotBlank()) {
                                viewModel.addBoss(newBossName)
                                newBossName = ""
                                showAddBossDialog = false
                            }
                        }
                    ) {
                        Text("Criar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            newBossName = ""
                            showAddBossDialog = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
private fun BossCard(
    bossModel: BossUiModel,
    onIncrementDeath: () -> Unit,
    onDecrementDeath: () -> Unit,
    onToggleTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAddLap: () -> Unit,
    onDeleteBoss: () -> Unit
) {
    val boss = bossModel.entity

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = boss.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onDeleteBoss) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Contador de Mortes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mortes",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = boss.deaths.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = onDecrementDeath,
                        enabled = boss.deaths > 0
                    ) {
                        Text("-", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = onIncrementDeath) {
                        Text("+", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Cronômetro
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cronômetro",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatDurationMillis(bossModel.displayElapsedMillis),
                    style = MaterialTheme.typography.displaySmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onToggleTimer,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (boss.isRunning) "Pausar" else "Iniciar")
                    }

                    FilledTonalButton(
                        onClick = onAddLap,
                        enabled = bossModel.displayElapsedMillis > 0L,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Lap / Fase")
                    }

                    OutlinedButton(
                        onClick = onResetTimer,
                        enabled = bossModel.displayElapsedMillis > 0L || boss.laps.isNotEmpty(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Zerar")
                    }
                }
            }

            // Lista de Laps
            if (boss.laps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Marcações de Tempo (Laps):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                boss.laps.forEachIndexed { index, lapMillis ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Fase / Lap #${index + 1}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = formatDurationMillis(lapMillis),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
