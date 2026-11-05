package io.github.carvalhoxx.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.carvalhoxx.data.local.entity.GameEntity
import io.github.carvalhoxx.ui.components.HudAtmosphericBackground
import io.github.carvalhoxx.ui.components.HudCrossedSwordsIcon
import io.github.carvalhoxx.ui.components.HudDiamondDivider
import io.github.carvalhoxx.ui.components.HudGlassPanel
import io.github.carvalhoxx.ui.components.HudMetallicButton
import io.github.carvalhoxx.ui.components.HudSkullIcon
import io.github.carvalhoxx.ui.components.HudTopHeader
import io.github.carvalhoxx.ui.theme.EldenGoldPrimary
import io.github.carvalhoxx.ui.theme.GameThemePreset
import io.github.carvalhoxx.ui.theme.LocalHudPalette
import io.github.carvalhoxx.ui.theme.toHudPalette
import io.github.carvalhoxx.ui.viewmodel.GameListViewModel
import io.github.carvalhoxx.ui.viewmodel.GameSummaryUiModel

private const val CUSTOM_GAME_OPTION = "Cadastrar jogo novo..."

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameListScreen(
    viewModel: GameListViewModel,
    onGameClick: (GameEntity) -> Unit
) {
    val gameSummaries by viewModel.gameSummaries.collectAsStateWithLifecycle()
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var selectedOption by rememberSaveable { mutableStateOf(GameThemePreset.ELDEN_RING.displayName) }
    var isCustomGame by rememberSaveable { mutableStateOf(false) }
    var customGameName by rememberSaveable { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val resetDialogState = {
        showAddDialog = false
        dropdownExpanded = false
        selectedOption = GameThemePreset.ELDEN_RING.displayName
        isCustomGame = false
        customGameName = ""
    }

    // Use Elden Ring gold/obsidian ambiance as the signature master HUD aesthetic
    val masterPalette = GameThemePreset.ELDEN_RING.toHudPalette()

    HudAtmosphericBackground(palette = masterPalette) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            HudTopHeader(
                title = "CONTADOR DE BOSSES",
                palette = masterPalette
            )

            // Hero Header Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ESCOLHA SEU REINO",
                    style = MaterialTheme.typography.labelMedium,
                    color = EldenGoldPrimary,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Contador de Bosses",
                    style = MaterialTheme.typography.headlineLarge,
                    color = masterPalette.textPrimary,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "◆",
                        color = EldenGoldPrimary,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${gameSummaries.size} REINOS REGISTRADOS",
                        style = MaterialTheme.typography.labelSmall,
                        color = masterPalette.textSecondary,
                        letterSpacing = 2.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Game Cards List
            if (gameSummaries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    HudGlassPanel(
                        palette = masterPalette,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HudCrossedSwordsIcon(color = EldenGoldPrimary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhum Reino Registrado",
                                style = MaterialTheme.typography.headlineSmall,
                                color = masterPalette.textPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Adicione um jogo abaixo (Elden Ring, Phantom Blade, Dark Souls ou personalizado) para registrar seus combates.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = masterPalette.textSecondary,
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
                    items(gameSummaries, key = { it.game.id }) { summary ->
                        HudGameCard(
                            summary = summary,
                            onClick = { onGameClick(summary.game) },
                            onDelete = { viewModel.deleteGame(summary.game) }
                        )
                    }
                }
            }

            // Bottom Metallic "+ Adicionar Jogo" Action Bar & Tagline
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HudMetallicButton(
                    text = "+ ADICIONAR JOGO",
                    filled = true,
                    palette = masterPalette,
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Cada queda é um passo adiante.",
                    style = MaterialTheme.typography.titleMedium,
                    color = masterPalette.textSecondary.copy(alpha = 0.85f),
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp
                )
            }
        }

        if (showAddDialog) {
            val effectiveGameName = if (isCustomGame) customGameName else selectedOption
            val detectedPreset = GameThemePreset.fromGameName(effectiveGameName)
            val dialogPalette = detectedPreset.toHudPalette()

            AlertDialog(
                onDismissRequest = resetDialogState,
                containerColor = Color(0xF514171B),
                shape = RoundedCornerShape(2.dp),
                title = {
                    Column {
                        Text(
                            text = "REGISTRAR NOVO REINO",
                            style = MaterialTheme.typography.labelSmall,
                            color = dialogPalette.accent,
                            letterSpacing = 2.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Adicionar Jogo",
                            style = MaterialTheme.typography.headlineSmall,
                            color = dialogPalette.textPrimary
                        )
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Selecione um jogo da lista ou escolha a última opção para digitar um novo:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = dialogPalette.textSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value = selectedOption,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Jogo") },
                                singleLine = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                                modifier = Modifier
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                    .fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                GameThemePreset.selectablePresets.forEach { preset ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(
                                                    text = preset.displayName,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = preset.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedOption = preset.displayName
                                            isCustomGame = false
                                            dropdownExpanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }

                                HorizontalDivider()

                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = CUSTOM_GAME_OPTION,
                                            style = MaterialTheme.typography.bodyLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = dialogPalette.accent
                                        )
                                    },
                                    onClick = {
                                        selectedOption = CUSTOM_GAME_OPTION
                                        isCustomGame = true
                                        dropdownExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }

                        if (isCustomGame) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = customGameName,
                                onValueChange = { customGameName = it },
                                label = { Text("Nome do Jogo") },
                                placeholder = { Text("Ex: Hollow Knight") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HudDiamondDivider(palette = dialogPalette)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TEMA: ${detectedPreset.displayName.uppercase()} — ${detectedPreset.description}",
                            style = MaterialTheme.typography.labelSmall,
                            color = dialogPalette.accent
                        )
                    }
                },
                confirmButton = {
                    HudMetallicButton(
                        text = "Confirmar",
                        filled = true,
                        enabled = effectiveGameName.isNotBlank(),
                        palette = dialogPalette,
                        onClick = {
                            if (effectiveGameName.isNotBlank()) {
                                viewModel.addGame(effectiveGameName)
                                resetDialogState()
                            }
                        },
                        modifier = Modifier.width(120.dp)
                    )
                },
                dismissButton = {
                    TextButton(onClick = resetDialogState) {
                        Text("Cancelar", color = dialogPalette.textSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun HudGameCard(
    summary: GameSummaryUiModel,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val gamePreset = GameThemePreset.fromGameName(summary.game.name)
    val cardPalette = gamePreset.toHudPalette()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(2.dp))
            .background(cardPalette.panelSurface)
            .border(1.dp, cardPalette.borderAccent.copy(alpha = 0.6f), RoundedCornerShape(2.dp))
            .clickable(onClick = onClick)
    ) {
        // Subtle thematic artwork banner inside the card
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRect(
                brush = Brush.horizontalGradient(
                    0.0f to cardPalette.groundBottom.copy(alpha = 0.92f),
                    0.6f to cardPalette.skyMid.copy(alpha = 0.72f),
                    1.0f to cardPalette.horizonGlow.copy(alpha = 0.55f)
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(cardPalette.accent.copy(alpha = 0.22f), Color.Transparent),
                    center = Offset(size.width * 0.88f, size.height * 0.35f),
                    radius = size.width * 0.45f
                ),
                center = Offset(size.width * 0.88f, size.height * 0.35f),
                radius = size.width * 0.45f
            )
        }

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
                        text = "◆",
                        color = cardPalette.accent,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = gamePreset.realmTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = cardPalette.accent,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = "REMOVER",
                    style = MaterialTheme.typography.labelSmall,
                    color = cardPalette.textMuted,
                    modifier = Modifier
                        .clickable(onClick = onDelete)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = summary.game.name,
                style = MaterialTheme.typography.headlineMedium,
                color = cardPalette.textPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HudCrossedSwordsIcon(
                        color = cardPalette.accent,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = if (summary.bossCount > 0) {
                            "${summary.defeatedCount}/${summary.bossCount} ABATIDOS"
                        } else {
                            "0 BOSSES"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (summary.bossCount > 0 && summary.defeatedCount == summary.bossCount) {
                            cardPalette.accentBright
                        } else {
                            cardPalette.textPrimary
                        },
                        letterSpacing = 1.6.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    HudSkullIcon(
                        color = cardPalette.accent,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = "${summary.totalDeaths} MORTES",
                        style = MaterialTheme.typography.labelSmall,
                        color = cardPalette.textSecondary,
                        letterSpacing = 1.6.sp
                    )
                }

                Text(
                    text = "ENTRAR ›",
                    style = MaterialTheme.typography.labelSmall,
                    color = cardPalette.accentBright,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}
