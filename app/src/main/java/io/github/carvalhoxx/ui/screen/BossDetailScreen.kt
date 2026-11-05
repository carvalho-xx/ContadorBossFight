package io.github.carvalhoxx.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
                        text = "◆",
                        color = palette.accent,
                        fontSize = 9.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (boss?.isRunning == true) "COMBATE EM ANDAMENTO" else "AGUARDANDO DESAFIANTE",
                        style = MaterialTheme.typography.labelSmall,
                        color = palette.textSecondary.copy(alpha = 0.85f),
                        letterSpacing = 2.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 3. Main Glassmorphic HUD Panel (exact match to reference.png)
                if (boss != null) {
                    HudGlassPanel(
                        palette = palette,
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
                                    enabled = boss.deaths > 0,
                                    palette = palette,
                                    onClick = { viewModel.decrementDeaths() }
                                )

                                Text(
                                    text = String.format(Locale.US, "%02d", boss.deaths),
                                    style = MaterialTheme.typography.displayLarge,
                                    color = palette.textPrimary
                                )

                                HudSquareIconButton(
                                    symbol = "+",
                                    enabled = true,
                                    palette = palette,
                                    onClick = { viewModel.incrementDeaths() }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Cada queda é um passo adiante.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = palette.textSecondary
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Horizontal Divider with Central Diamond
                            HudDiamondDivider(palette = palette)

                            Spacer(modifier = Modifier.height(20.dp))

                            // TEMPO DE COMBATE
                            Text(
                                text = "TEMPO DE COMBATE",
                                style = MaterialTheme.typography.labelMedium,
                                color = palette.accent,
                                letterSpacing = 2.8.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // 00:00.00
                            Text(
                                text = formatDurationMillis(uiState.displayElapsedMillis),
                                style = MaterialTheme.typography.displaySmall,
                                color = palette.textPrimary,
                                fontWeight = FontWeight.Light
                            )

                            Spacer(modifier = Modifier.height(22.dp))

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
        }
    }
}
