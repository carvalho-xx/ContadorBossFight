package io.github.carvalhoxx.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.carvalhoxx.ui.theme.GameThemePreset
import io.github.carvalhoxx.ui.theme.HudPalette
import io.github.carvalhoxx.ui.theme.LocalHudPalette

@Composable
fun HudAtmosphericBackground(
    modifier: Modifier = Modifier,
    palette: HudPalette = LocalHudPalette.current,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Atmospheric Sky & Horizon Vertical Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to palette.skyTop,
                    0.32f to palette.skyMid,
                    0.52f to palette.horizonGlow.copy(alpha = 0.85f),
                    0.78f to palette.groundBottom,
                    1.0f to Color(0xFF050708)
                )
            )

            // 2. Radiant Mist Glow on Upper Right (matching reference.png Erdtree / Blood Moon glow)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        palette.accentBright.copy(alpha = 0.24f),
                        palette.horizonGlow.copy(alpha = 0.14f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.78f, h * 0.24f),
                    radius = w * 0.82f
                ),
                center = Offset(w * 0.78f, h * 0.24f),
                radius = w * 0.82f
            )

            // 3. Game-specific Silhouette Scene (Castle / Pagoda / Cathedral)
            when (palette.preset) {
                GameThemePreset.ELDEN_RING -> drawEldenRingBackdrop(w, h, palette)
                GameThemePreset.PHANTOM_BLADE -> drawPhantomBladeBackdrop(w, h, palette)
                else -> drawDarkFantasyBackdrop(w, h, palette)
            }

            // 4. Floating Embers / Ash / Blood Petals
            drawAtmosphericParticles(w, h, palette)

            // 5. Dark Cinematic Vignette at Top and Bottom for HUD legibility
            drawRect(
                brush = Brush.verticalGradient(
                    0.0f to Color.Black.copy(alpha = 0.55f),
                    0.18f to Color.Transparent,
                    0.72f to Color.Transparent,
                    1.0f to Color.Black.copy(alpha = 0.82f)
                )
            )
        }

        content()
    }
}

private fun DrawScope.drawEldenRingBackdrop(w: Float, h: Float, palette: HudPalette) {
    val silhouetteColor = Color(0xFF141B1D).copy(alpha = 0.82f)
    val distantColor = Color(0xFF1F2A2C).copy(alpha = 0.6f)

    // Distant Stormveil Castle spires in center-upper area
    val castlePath = Path().apply {
        moveTo(w * 0.34f, h * 0.45f)
        lineTo(w * 0.36f, h * 0.29f)
        lineTo(w * 0.38f, h * 0.25f)
        lineTo(w * 0.40f, h * 0.29f)
        lineTo(w * 0.43f, h * 0.27f)
        lineTo(w * 0.46f, h * 0.17f) // Main spire
        lineTo(w * 0.48f, h * 0.23f)
        lineTo(w * 0.51f, h * 0.20f)
        lineTo(w * 0.54f, h * 0.27f)
        lineTo(w * 0.57f, h * 0.24f)
        lineTo(w * 0.59f, h * 0.30f)
        lineTo(w * 0.63f, h * 0.45f)
        close()
    }
    drawPath(castlePath, color = distantColor)

    // Gnarled branches reaching from top-right corner
    val branchColor = Color(0xFF181611).copy(alpha = 0.78f)
    val branchStroke = Stroke(width = 3.5f, cap = StrokeCap.Round)
    val branch1 = Path().apply {
        moveTo(w * 1.02f, h * 0.04f)
        cubicTo(w * 0.85f, h * 0.08f, w * 0.72f, h * 0.12f, w * 0.56f, h * 0.06f)
    }
    val branch2 = Path().apply {
        moveTo(w * 1.02f, h * 0.14f)
        cubicTo(w * 0.88f, h * 0.17f, w * 0.75f, h * 0.22f, w * 0.62f, h * 0.25f)
    }
    val branch3 = Path().apply {
        moveTo(w * 0.82f, h * 0.16f)
        cubicTo(w * 0.74f, h * 0.14f, w * 0.68f, h * 0.17f, w * 0.60f, h * 0.15f)
    }
    drawPath(branch1, color = branchColor, style = branchStroke)
    drawPath(branch2, color = branchColor, style = branchStroke)
    drawPath(branch3, color = branchColor, style = Stroke(width = 2.2f, cap = StrokeCap.Round))

    // Lone Tarnished warrior silhouette at bottom path
    val warriorX = w * 0.49f
    val warriorTopY = h * 0.78f
    val capePath = Path().apply {
        moveTo(warriorX, warriorTopY)
        lineTo(warriorX - 18f, warriorTopY + 68f)
        lineTo(warriorX + 22f, warriorTopY + 72f)
        close()
    }
    drawPath(capePath, color = silhouetteColor)
    drawCircle(
        color = silhouetteColor,
        radius = 7f,
        center = Offset(warriorX, warriorTopY - 5f)
    )
    // Golden glowing grace aura near the warrior
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(palette.accent.copy(alpha = 0.18f), Color.Transparent),
            center = Offset(warriorX, warriorTopY + 35f),
            radius = 120f
        ),
        center = Offset(warriorX, warriorTopY + 35f),
        radius = 120f
    )
}

private fun DrawScope.drawPhantomBladeBackdrop(w: Float, h: Float, palette: HudPalette) {
    // Crimson Blood Moon in upper right
    drawCircle(
        color = palette.accent.copy(alpha = 0.28f),
        radius = w * 0.22f,
        center = Offset(w * 0.76f, h * 0.20f)
    )
    drawCircle(
        color = palette.accentBright.copy(alpha = 0.15f),
        radius = w * 0.30f,
        center = Offset(w * 0.76f, h * 0.20f)
    )

    // Oriental Pagoda / Temple Roof Silhouette in center mist
    val pagodaColor = Color(0xFF1A0D0F).copy(alpha = 0.75f)
    val pagodaPath = Path().apply {
        // Top tier roof with curved eaves
        moveTo(w * 0.36f, h * 0.23f)
        quadraticTo(w * 0.50f, h * 0.18f, w * 0.64f, h * 0.23f)
        lineTo(w * 0.58f, h * 0.25f)
        lineTo(w * 0.58f, h * 0.29f)
        // Second tier roof
        lineTo(w * 0.68f, h * 0.31f)
        quadraticTo(w * 0.50f, h * 0.28f, w * 0.32f, h * 0.31f)
        lineTo(w * 0.42f, h * 0.29f)
        lineTo(w * 0.42f, h * 0.25f)
        close()
    }
    drawPath(pagodaPath, color = pagodaPathColor(pagodaColor))

    // Diagonal rain / katana slash lines
    for (i in 0..14) {
        val startX = (w * 0.08f * i)
        val startY = (h * 0.05f * (i % 5))
        drawLine(
            color = palette.accentBright.copy(alpha = 0.10f),
            start = Offset(startX + 60f, startY),
            end = Offset(startX - 20f, startY + 180f),
            strokeWidth = 1.2f
        )
    }
}

private fun pagodaPathColor(c: Color): Color = c

private fun DrawScope.drawDarkFantasyBackdrop(w: Float, h: Float, palette: HudPalette) {
    val archColor = Color(0xFF13171D).copy(alpha = 0.7f)
    val spirePath = Path().apply {
        moveTo(w * 0.38f, h * 0.42f)
        lineTo(w * 0.44f, h * 0.21f)
        lineTo(w * 0.50f, h * 0.16f)
        lineTo(w * 0.56f, h * 0.21f)
        lineTo(w * 0.62f, h * 0.42f)
        close()
    }
    drawPath(spirePath, color = archColor)
}

private fun DrawScope.drawAtmosphericParticles(w: Float, h: Float, palette: HudPalette) {
    val particleOffsets = listOf(
        Offset(0.12f, 0.18f), Offset(0.84f, 0.14f), Offset(0.68f, 0.22f),
        Offset(0.24f, 0.32f), Offset(0.76f, 0.36f), Offset(0.15f, 0.64f),
        Offset(0.88f, 0.58f), Offset(0.32f, 0.76f), Offset(0.66f, 0.74f),
        Offset(0.44f, 0.82f), Offset(0.79f, 0.84f), Offset(0.21f, 0.88f)
    )
    particleOffsets.forEachIndexed { index, ratio ->
        val radius = if (index % 3 == 0) 3.2f else 2.0f
        val alpha = if (index % 2 == 0) 0.55f else 0.32f
        drawCircle(
            color = palette.accentBright.copy(alpha = alpha),
            radius = radius,
            center = Offset(w * ratio.x, h * ratio.y)
        )
    }
}

@Composable
fun HudGlassPanel(
    modifier: Modifier = Modifier,
    palette: HudPalette = LocalHudPalette.current,
    borderColor: Color = palette.borderAccent,
    backgroundColor: Color = palette.panelSurface,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(backgroundColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(2.dp)),
        content = content
    )
}

@Composable
fun HudDiamondDivider(
    modifier: Modifier = Modifier,
    palette: HudPalette = LocalHudPalette.current
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(palette.borderAccent.copy(alpha = 0.35f))
        )
        Text(
            text = "◆",
            color = palette.accent,
            fontSize = 10.sp,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(palette.borderAccent.copy(alpha = 0.35f))
        )
    }
}

@Composable
fun HudTopHeader(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionLabel: String? = null,
    palette: HudPalette = LocalHudPalette.current
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (onBackClick != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "‹",
                    color = palette.textPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }

        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = palette.textPrimary.copy(alpha = 0.9f),
            letterSpacing = 3.2.sp
        )

        if (onActionClick != null) {
            Box(
                modifier = Modifier
                    .clickable(onClick = onActionClick)
                    .padding(4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = actionLabel ?: "⊶",
                    color = palette.textSecondary,
                    fontSize = if (actionLabel != null) 11.sp else 20.sp,
                    letterSpacing = 1.5.sp
                )
            }
        } else {
            Spacer(modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun HudMetallicButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    enabled: Boolean = true,
    leadingSymbol: String? = null,
    palette: HudPalette = LocalHudPalette.current
) {
    val bgColor = when {
        !enabled -> palette.buttonSurface.copy(alpha = 0.45f)
        filled -> palette.accent
        else -> palette.buttonSurface
    }
    val borderColor = when {
        !enabled -> palette.borderAccent.copy(alpha = 0.18f)
        filled -> palette.accentBright.copy(alpha = 0.85f)
        else -> palette.borderAccent.copy(alpha = 0.48f)
    }
    val contentColor = when {
        !enabled -> palette.textMuted.copy(alpha = 0.5f)
        filled -> palette.onAccent
        else -> palette.textPrimary
    }

    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(2.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(2.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingSymbol != null) {
                Text(
                    text = leadingSymbol,
                    color = contentColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (filled) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HudSquareIconButton(
    symbol: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    palette: HudPalette = LocalHudPalette.current
) {
    val borderColor = if (enabled) {
        palette.borderAccent.copy(alpha = 0.55f)
    } else {
        palette.borderAccent.copy(alpha = 0.2f)
    }
    val symbolColor = if (enabled) {
        palette.accent
    } else {
        palette.textMuted.copy(alpha = 0.4f)
    }

    Surface(
        modifier = Modifier
            .size(54.dp)
            .clip(RoundedCornerShape(2.dp))
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(2.dp),
        color = palette.buttonSurface,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                color = symbolColor,
                fontSize = 24.sp,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun HudSkullIcon(
    modifier: Modifier = Modifier,
    color: Color = LocalHudPalette.current.accent
) {
    Canvas(modifier = modifier.size(16.dp)) {
        val w = size.width
        val h = size.height
        // Cranium dome
        drawCircle(
            color = color,
            radius = w * 0.38f,
            center = Offset(w * 0.5f, h * 0.42f),
            style = Stroke(width = 1.6.dp.toPx())
        )
        // Jaw
        drawRect(
            color = color,
            topLeft = Offset(w * 0.32f, h * 0.65f),
            size = androidx.compose.ui.geometry.Size(w * 0.36f, h * 0.22f),
            style = Stroke(width = 1.4.dp.toPx())
        )
        // Eyes
        drawCircle(
            color = color,
            radius = w * 0.08f,
            center = Offset(w * 0.38f, h * 0.42f)
        )
        drawCircle(
            color = color,
            radius = w * 0.08f,
            center = Offset(w * 0.62f, h * 0.42f)
        )
    }
}

@Composable
fun HudCrossedSwordsIcon(
    modifier: Modifier = Modifier,
    color: Color = LocalHudPalette.current.accent
) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.8.dp.toPx()

        // Sword 1 (bottom-left to top-right)
        drawLine(
            color = color,
            start = Offset(w * 0.18f, h * 0.82f),
            end = Offset(w * 0.82f, h * 0.18f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Crossguard 1
        drawLine(
            color = color,
            start = Offset(w * 0.18f, h * 0.64f),
            end = Offset(w * 0.36f, h * 0.82f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )

        // Sword 2 (bottom-right to top-left)
        drawLine(
            color = color,
            start = Offset(w * 0.82f, h * 0.82f),
            end = Offset(w * 0.18f, h * 0.18f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        // Crossguard 2
        drawLine(
            color = color,
            start = Offset(w * 0.64f, h * 0.82f),
            end = Offset(w * 0.82f, h * 0.64f),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
    }
}
