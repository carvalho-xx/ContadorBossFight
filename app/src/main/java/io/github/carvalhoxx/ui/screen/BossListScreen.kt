package io.github.carvalhoxx.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import io.github.carvalhoxx.ui.components.HudTopHeader
import io.github.carvalhoxx.ui.theme.ContadorBossFightTheme
import io.github.carvalhoxx.ui.theme.LocalHudPalette
import io.github.carvalhoxx.ui.util.formatDurationMillis
import io.github.carvalhoxx.ui.util.hmsToMillis
import io.github.carvalhoxx.ui.util.toRomanNumeral
import io.github.carvalhoxx.ui.viewmodel.BossListViewModel
import io.github.carvalhoxx.ui.viewmodel.BossUiModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BossListScreen(
    viewModel: BossListViewModel,
    onBossClick: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddBossDialog by rememberSaveable { mutableStateOf(false) }
    var newBossName by rememberSaveable { mutableStateOf("") }
    var initialDeathsInput by rememberSaveable { mutableStateOf("") }
    var initialHoursInput by rememberSaveable { mutableStateOf("") }
    var initialMinutesInput by rememberSaveable { mutableStateOf("") }
    var initialSecondsInput by rememberSaveable { mutableStateOf("") }
    var initialIsDefeated by rememberSaveable { mutableStateOf(false) }

    val resetAddBossDialog = {
        showAddBossDialog = false
        newBossName = ""
        initialDeathsInput = ""
        initialHoursInput = ""
        initialMinutesInput = ""
        initialSecondsInput = ""
        initialIsDefeated = false
    }

    val defeatedCount = uiState.bosses.count { it.entity.isDefeated }

    ContadorBossFightTheme(currentGameName = uiState.game?.name) {
        val palette = LocalHudPalette.current

        HudAtmosphericBackground(palette = palette) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                HudTopHeader(
                    title = "CONTADOR DE BOSSES",
                    onBackClick = onNavigateBack,
                    palette = palette
                )

                // Game Header Banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = palette.preset.realmTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.accent,
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = uiState.game?.name ?: "Chefes",
                        style = MaterialTheme.typography.headlineLarge,
                        color = palette.textPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "◆",
                            color = palette.accent,
                            fontSize = 9.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.bosses.isNotEmpty()) {
                                "SELECIONE SEU ADVERSÁRIO • $defeatedCount/${uiState.bosses.size} ABATIDOS"
                            } else {
                                "SELECIONE SEU ADVERSÁRIO"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.bosses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HudGlassPanel(
                            palette = palette,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                HudSkullIcon(color = palette.accent)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Nenhum Adversário Registrado",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = palette.textPrimary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Adicione um boss abaixo (ex: Margit, Malenia, Artorias) para iniciar o combate.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(uiState.bosses, key = { it.entity.id }) { bossModel ->
                            HudBossSummaryCard(
                                bossModel = bossModel,
                                onEnterCombat = { onBossClick(bossModel.entity.id) },
                                onDeleteBoss = { viewModel.deleteBoss(bossModel.entity) }
                            )
                        }
                    }
                }

                // Bottom Metallic Button + Rally Quote
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HudMetallicButton(
                        text = "+ ADICIONAR BOSS",
                        filled = true,
                        palette = palette,
                        onClick = { showAddBossDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = palette.preset.rallyQuote,
                        style = MaterialTheme.typography.titleMedium,
                        color = palette.textSecondary.copy(alpha = 0.88f),
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp
                    )
                }
            }

            if (showAddBossDialog) {
                AlertDialog(
                    onDismissRequest = resetAddBossDialog,
                    containerColor = Color(0xF514171B),
                    shape = RoundedCornerShape(2.dp),
                    title = {
                        Column {
                            Text(
                                text = "NOVO CONFRONTO",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.accent,
                                letterSpacing = 2.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Novo Boss",
                                style = MaterialTheme.typography.headlineSmall,
                                color = palette.textPrimary
                            )
                        }
                    },
                    text = {
                        Column {
                            Text(
                                text = "Informe o nome do chefe e, se já o enfrentou antes, os dados iniciais (opcional):",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = newBossName,
                                onValueChange = { newBossName = it },
                                label = { Text("Nome do Boss") },
                                placeholder = { Text("Ex: Margit, o Agouro Caído") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = initialDeathsInput,
                                onValueChange = { value ->
                                    if (value.length <= 5 && value.all { it.isDigit() }) {
                                        initialDeathsInput = value
                                    }
                                },
                                label = { Text("Mortes já registradas (opcional)") },
                                placeholder = { Text("0") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "TEMPO JÁ REGISTRADO (OPCIONAL)",
                                style = MaterialTheme.typography.labelSmall,
                                color = palette.accent,
                                letterSpacing = 1.8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = initialHoursInput,
                                    onValueChange = { value ->
                                        if (value.length <= 4 && value.all { it.isDigit() }) {
                                            initialHoursInput = value
                                        }
                                    },
                                    label = { Text("Horas") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = initialMinutesInput,
                                    onValueChange = { value ->
                                        if (value.length <= 2 && value.all { it.isDigit() }) {
                                            initialMinutesInput = value
                                        }
                                    },
                                    label = { Text("Min") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = initialSecondsInput,
                                    onValueChange = { value ->
                                        if (value.length <= 2 && value.all { it.isDigit() }) {
                                            initialSecondsInput = value
                                        }
                                    },
                                    label = { Text("Seg") },
                                    placeholder = { Text("0") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { initialIsDefeated = !initialIsDefeated },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = initialIsDefeated,
                                    onCheckedChange = { initialIsDefeated = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = palette.accent,
                                        checkmarkColor = palette.onAccent,
                                        uncheckedColor = palette.textSecondary
                                    )
                                )
                                Text(
                                    text = "Boss já derrotado (Inimigo Abatido)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = palette.textPrimary
                                )
                            }
                        }
                    },
                    confirmButton = {
                        HudMetallicButton(
                            text = "Criar",
                            filled = true,
                            enabled = newBossName.isNotBlank(),
                            palette = palette,
                            onClick = {
                                if (newBossName.isNotBlank()) {
                                    val deaths = initialDeathsInput.toIntOrNull() ?: 0
                                    val h = initialHoursInput.toIntOrNull() ?: 0
                                    val m = initialMinutesInput.toIntOrNull() ?: 0
                                    val s = initialSecondsInput.toIntOrNull() ?: 0
                                    viewModel.addBoss(
                                        name = newBossName,
                                        initialDeaths = deaths,
                                        initialDurationMillis = hmsToMillis(h, m, s),
                                        isDefeated = initialIsDefeated
                                    )
                                    resetAddBossDialog()
                                }
                            },
                            modifier = Modifier.width(110.dp)
                        )
                    },
                    dismissButton = {
                        TextButton(onClick = resetAddBossDialog) {
                            Text("Cancelar", color = palette.textSecondary)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HudBossSummaryCard(
    bossModel: BossUiModel,
    onEnterCombat: () -> Unit,
    onDeleteBoss: () -> Unit
) {
    val palette = LocalHudPalette.current
    val boss = bossModel.entity
    val attemptNumber = boss.deaths + 1
    val isDefeated = boss.isDefeated

    val cardBorderColor = if (isDefeated) {
        palette.accentBright.copy(alpha = 0.85f)
    } else {
        palette.borderAccent
    }
    val cardBgColor = if (isDefeated) {
        palette.panelInnerSurface
    } else {
        palette.panelSurface
    }

    HudGlassPanel(
        palette = palette,
        borderColor = cardBorderColor,
        backgroundColor = cardBgColor,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onEnterCombat)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isDefeated) "★" else "◆",
                        color = if (isDefeated) palette.accentBright else palette.accent,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when {
                            isDefeated -> "INIMIGO ABATIDO"
                            boss.isRunning -> "COMBATE EM ANDAMENTO"
                            else -> "TENTATIVA ${toRomanNumeral(attemptNumber)}"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isDefeated -> palette.accentBright
                            boss.isRunning -> palette.accentBright
                            else -> palette.textSecondary
                        },
                        fontWeight = if (isDefeated) FontWeight.Bold else FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = "EXCLUIR",
                    style = MaterialTheme.typography.labelSmall,
                    color = palette.textMuted,
                    modifier = Modifier
                        .clickable(onClick = onDeleteBoss)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = boss.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isDefeated) palette.accentBright else palette.textPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HudDiamondDivider(palette = palette)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MORTES",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = String.format(Locale.US, "%02d", boss.deaths),
                            style = MaterialTheme.typography.headlineSmall,
                            color = palette.textPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Column {
                        Text(
                            text = if (isDefeated) "TEMPO FINAL" else "TEMPO DE COMBATE",
                            style = MaterialTheme.typography.labelSmall,
                            color = palette.textSecondary,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = formatDurationMillis(bossModel.displayElapsedMillis),
                            style = MaterialTheme.typography.titleLarge,
                            fontSize = 18.sp,
                            maxLines = 1,
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Light,
                            color = when {
                                isDefeated -> palette.accentBright
                                boss.isRunning -> palette.accentBright
                                else -> palette.textPrimary
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                HudMetallicButton(
                    text = if (isDefeated) "Abatido" else "Lutar",
                    filled = !isDefeated,
                    leadingSymbol = if (isDefeated) "✓" else "⚔",
                    palette = palette,
                    onClick = onEnterCombat,
                    modifier = Modifier.width(110.dp)
                )
            }
        }
    }
}
