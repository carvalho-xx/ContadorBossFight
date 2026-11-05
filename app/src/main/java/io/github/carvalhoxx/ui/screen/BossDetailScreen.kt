package io.github.carvalhoxx.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.carvalhoxx.ui.components.HudAtmosphericBackground
import io.github.carvalhoxx.ui.components.HudCrossedSwordsIcon
import io.github.carvalhoxx.ui.components.HudDiamondDivider
import io.github.carvalhoxx.ui.components.HudGlassPanel
import io.github.carvalhoxx.ui.components.HudMetallicButton
import io.github.carvalhoxx.ui.components.HudSkullIcon
import io.github.carvalhoxx.ui.components.HudSquareIconButton
import io.github.carvalhoxx.ui.components.HudTopHeader
import io.github.carvalhoxx.ui.theme.ContadorBossFightTheme
import io.github.carvalhoxx.ui.theme.LocalHudPalette
import io.github.carvalhoxx.ui.util.formatDurationMillis
import io.github.carvalhoxx.ui.util.hmsToMillis
import io.github.carvalhoxx.ui.util.toRomanNumeral
import io.github.carvalhoxx.ui.viewmodel.BossDetailViewModel
import java.util.Locale

@Composable
fun BossDetailScreen(
    viewModel: BossDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val boss = uiState.boss

    var showEditTimeDialog by rememberSaveable { mutableStateOf(false) }
    var hoursInput by rememberSaveable { mutableStateOf("0") }
    var minutesInput by rememberSaveable { mutableStateOf("0") }
    var secondsInput by rememberSaveable { mutableStateOf("0") }

    var showEditDeathsDialog by rememberSaveable { mutableStateOf(false) }
    var deathsInput by rememberSaveable { mutableStateOf("0") }

    val openEditTimeDialog = {
        val currentMillis = uiState.displayElapsedMillis.coerceAtLeast(0L)
        val h = currentMillis / 3_600_000L
        val m = (currentMillis % 3_600_000L) / 60_000L
        val s = (currentMillis % 60_000L) / 1_000L
        hoursInput = h.toString()
        minutesInput = m.toString()
        secondsInput = s.toString()
        showEditTimeDialog = true
    }

    val openEditDeathsDialog = {
        deathsInput = (boss?.deaths ?: 0).toString()
        showEditDeathsDialog = true
    }

    ContadorBossFightTheme(currentGameName = uiState.game?.name) {
        val palette = LocalHudPalette.current

        HudAtmosphericBackground(palette = palette) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Top Bar: <   CONTADOR DE BOSSES   EXCLUIR
                HudTopHeader(
                    title = "CONTADOR DE BOSSES",
                    onBackClick = onNavigateBack,
                    onActionClick = { viewModel.deleteBoss(onNavigateBack) },
                    actionLabel = "EXCLUIR",
                    palette = palette
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Hero Header (Game Realm / Location -> Boss Name -> Combat Status)
                Text(
                    text = palette.preset.realmTitle.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    color = palette.accent,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = boss?.name ?: "...",
                    style = MaterialTheme.typography.headlineLarge,
                    color = palette.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (boss?.isDefeated == true) "★" else "◆",
                        color = if (boss?.isDefeated == true) palette.accentBright else palette.accent,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            boss?.isDefeated == true -> "GRANDE INIMIGO ABATIDO"
                            boss?.isRunning == true -> "COMBATE EM ANDAMENTO"
                            else -> "AGUARDANDO DESAFIANTE"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (boss?.isDefeated == true) {
                            palette.accentBright
                        } else {
                            palette.textSecondary.copy(alpha = 0.85f)
                        },
                        fontWeight = if (boss?.isDefeated == true) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 2.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 3. Main Glassmorphic HUD Panel
                if (boss != null) {
                    if (boss.isDefeated) {
                        // Soulsborne Victory Banner ("INIMIGO ABATIDO")
                        HudGlassPanel(
                            palette = palette,
                            borderColor = palette.accentBright.copy(alpha = 0.85f),
                            backgroundColor = palette.panelInnerSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 14.dp, horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "★  INIMIGO ABATIDO  ★",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = palette.accentBright,
                                    letterSpacing = 4.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Vitória alcançada após ${boss.deaths} ${if (boss.deaths == 1) "morte" else "mortes"} (${boss.deaths + 1}ª tentativa)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textPrimary.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    HudGlassPanel(
                        palette = palette,
                        borderColor = if (boss.isDefeated) palette.accentBright.copy(alpha = 0.7f) else palette.borderAccent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 22.dp, vertical = 26.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Skull + O PREÇO DO DESAFIO
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                HudSkullIcon(color = palette.accent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "O PREÇO DO DESAFIO",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = palette.accent,
                                    letterSpacing = 2.6.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Contador de Mortes",
                                style = MaterialTheme.typography.headlineMedium,
                                color = palette.textPrimary
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            // [ - ]    07    [ + ]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HudSquareIconButton(
                                    symbol = "−",
                                    enabled = !boss.isDefeated && boss.deaths > 0,
                                    palette = palette,
                                    onClick = { viewModel.decrementDeaths() }
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable(onClick = openEditDeathsDialog)
                                        .padding(horizontal = 12.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = String.format(Locale.US, "%02d", boss.deaths),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = if (boss.isDefeated) palette.accentBright else palette.textPrimary
                                    )
                                    Text(
                                        text = "✎ DEFINIR VALOR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = palette.accent.copy(alpha = 0.8f),
                                        fontSize = 9.sp,
                                        letterSpacing = 1.5.sp
                                    )
                                }

                                HudSquareIconButton(
                                    symbol = "+",
                                    enabled = !boss.isDefeated,
                                    palette = palette,
                                    onClick = { viewModel.incrementDeaths() }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = if (boss.isDefeated) {
                                    "Confronto encerrado com glória."
                                } else {
                                    "Cada queda é um passo adiante."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Horizontal Divider with Central Diamond
                            HudDiamondDivider(palette = palette)

                            Spacer(modifier = Modifier.height(20.dp))

                            // TEMPO DE COMBATE
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = if (boss.isDefeated) "TEMPO FINAL DE COMBATE" else "TEMPO DE COMBATE",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = palette.accent,
                                    letterSpacing = 2.8.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 00:00:00.00 (Clickable to manually set time)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(onClick = openEditTimeDialog)
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = formatDurationMillis(uiState.displayElapsedMillis),
                                    style = MaterialTheme.typography.displaySmall,
                                    fontSize = 36.sp,
                                    color = if (boss.isDefeated) palette.accentBright else palette.textPrimary,
                                    fontWeight = FontWeight.Light
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✎ DEFINIR TEMPO MANUALMENTE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.accent.copy(alpha = 0.85f),
                                    fontSize = 9.sp,
                                    letterSpacing = 1.6.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            if (!boss.isDefeated) {
                                // Horizontal Action Buttons: [ ▷ Iniciar ] [ Marcar ] [ Zerar ]
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HudMetallicButton(
                                        text = if (boss.isRunning) "Pausar" else "Iniciar",
                                        leadingSymbol = if (boss.isRunning) "❚❚" else "▷",
                                        filled = true,
                                        palette = palette,
                                        onClick = { viewModel.toggleTimer() },
                                        modifier = Modifier.weight(1f)
                                    )

                                    HudMetallicButton(
                                        text = "Marcar",
                                        filled = false,
                                        enabled = uiState.displayElapsedMillis > 0L,
                                        palette = palette,
                                        onClick = { viewModel.addLap() },
                                        modifier = Modifier.weight(1f)
                                    )

                                    HudMetallicButton(
                                        text = "Zerar",
                                        filled = false,
                                        enabled = uiState.displayElapsedMillis > 0L || boss.laps.isNotEmpty(),
                                        palette = palette,
                                        onClick = { viewModel.resetTimer() },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            // "Matar Boss" / "Reabrir Combate" Primary Victory Action
                            HudMetallicButton(
                                text = if (boss.isDefeated) {
                                    "REABRIR COMBATE (NG+)"
                                } else {
                                    "MATAR BOSS (INIMIGO ABATIDO)"
                                },
                                leadingSymbol = if (boss.isDefeated) "↺" else "⚔",
                                filled = !boss.isDefeated,
                                palette = palette,
                                onClick = { viewModel.toggleDefeated() },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Laps Section (Phase Transitions)
                            if (boss.laps.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(20.dp))
                                HudDiamondDivider(palette = palette)
                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "TRANSIÇÕES DE FASE (MARCAS)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = palette.accent,
                                    letterSpacing = 2.4.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                boss.laps.forEachIndexed { index, lapMillis ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "FASE ${toRomanNumeral(index + 1)}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = palette.textSecondary,
                                            letterSpacing = 1.8.sp
                                        )
                                        Text(
                                            text = formatDurationMillis(lapMillis),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = palette.accentBright,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. Status Row below card: • Progresso salvo          TENTATIVA VIII
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(palette.accent)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Progresso salvo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                        }

                        Text(
                            text = "TENTATIVA ${toRomanNumeral(boss.deaths + 1)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(28.dp))

                // 5. Bottom Crest & Quote: Crossed Swords + dynamic rallyQuote
                HudCrossedSwordsIcon(color = palette.accent)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = palette.preset.rallyQuote,
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.textPrimary.copy(alpha = 0.9f),
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            if (showEditTimeDialog) {
                AlertDialog(
                    onDismissRequest = { showEditTimeDialog = false },
                    containerColor = Color(0xF514171B),
                    shape = RoundedCornerShape(2.dp),
                    title = {
                        Column {
                            Text(
                                text = "AJUSTE DE CRONÔMETRO",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.accent,
                                letterSpacing = 2.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Definir Tempo de Combate",
                                style = MaterialTheme.typography.headlineSmall,
                                color = palette.textPrimary
                            )
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = "Informe o tempo acumulado para este boss (horas, minutos e segundos):",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = hoursInput,
                                    onValueChange = { value ->
                                        if (value.length <= 4 && value.all { it.isDigit() }) {
                                            hoursInput = value
                                        }
                                    },
                                    label = { Text("Horas") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = minutesInput,
                                    onValueChange = { value ->
                                        if (value.length <= 2 && value.all { it.isDigit() }) {
                                            minutesInput = value
                                        }
                                    },
                                    label = { Text("Min") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = secondsInput,
                                    onValueChange = { value ->
                                        if (value.length <= 2 && value.all { it.isDigit() }) {
                                            secondsInput = value
                                        }
                                    },
                                    label = { Text("Seg") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        HudMetallicButton(
                            text = "Salvar",
                            filled = true,
                            palette = palette,
                            onClick = {
                                val h = hoursInput.toIntOrNull() ?: 0
                                val m = minutesInput.toIntOrNull() ?: 0
                                val s = secondsInput.toIntOrNull() ?: 0
                                viewModel.setTimerDuration(hmsToMillis(h, m, s))
                                showEditTimeDialog = false
                            },
                            modifier = Modifier.width(110.dp)
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditTimeDialog = false }) {
                            Text("Cancelar", color = palette.textSecondary)
                        }
                    }
                )
            }

            if (showEditDeathsDialog) {
                AlertDialog(
                    onDismissRequest = { showEditDeathsDialog = false },
                    containerColor = Color(0xF514171B),
                    shape = RoundedCornerShape(2.dp),
                    title = {
                        Column {
                            Text(
                                text = "O PREÇO DO DESAFIO",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.accent,
                                letterSpacing = 2.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Definir Total de Mortes",
                                style = MaterialTheme.typography.headlineSmall,
                                color = palette.textPrimary
                            )
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = "Digite a quantidade de mortes acumuladas neste boss:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = deathsInput,
                                onValueChange = { value ->
                                    if (value.length <= 5 && value.all { it.isDigit() }) {
                                        deathsInput = value
                                    }
                                },
                                label = { Text("Mortes") },
                                placeholder = { Text("0") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        HudMetallicButton(
                            text = "Salvar",
                            filled = true,
                            palette = palette,
                            onClick = {
                                val d = deathsInput.toIntOrNull() ?: 0
                                viewModel.setDeaths(d)
                                showEditDeathsDialog = false
                            },
                            modifier = Modifier.width(110.dp)
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditDeathsDialog = false }) {
                            Text("Cancelar", color = palette.textSecondary)
                        }
                    }
                )
            }
        }
    }
}
