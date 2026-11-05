package io.github.carvalhoxx.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class GameThemePreset(
    val displayName: String,
    val description: String,
    val realmTitle: String,
    val rallyQuote: String
) {
    ELDEN_RING(
        displayName = "Elden Ring",
        description = "Dourado Áureo & Névoa de Stormveil",
        realmTitle = "AS TERRAS INTERMÉDIAS",
        rallyQuote = "Levante-se, Maculado."
    ),
    PHANTOM_BLADE(
        displayName = "Phantom Blade",
        description = "Vermelho Sangue & Sombra Oriental",
        realmTitle = "O MUNDO FANTASMA",
        rallyQuote = "Empunhe sua lâmina mais uma vez."
    ),
    DARK_SOULS_1(
        displayName = "Dark Souls",
        description = "Chama Primordial & Cinzas de Lordran",
        realmTitle = "REINO DE LORDRAN",
        rallyQuote = "Não ouse se tornar Vazio."
    ),
    DARK_SOULS_2(
        displayName = "Dark Souls II",
        description = "Brasa de Majula & Maldição de Drangleic",
        realmTitle = "REINO DE DRANGLEIC",
        rallyQuote = "Busque força. O resto virá."
    ),
    DARK_SOULS_3(
        displayName = "Dark Souls III",
        description = "Fogo dos Lordes & Cinzas de Lothric",
        realmTitle = "REINO DE LOTHRIC",
        rallyQuote = "Que as chamas guiem o seu caminho."
    ),
    BLOODBORNE(
        displayName = "Bloodborne",
        description = "Sangue Pálido & Noite de Yharnam",
        realmTitle = "PESADELO DE YHARNAM",
        rallyQuote = "Um caçador deve caçar."
    ),
    SEKIRO(
        displayName = "Sekiro",
        description = "Vermelhão Shinobi & Névoa de Ashina",
        realmTitle = "TERRAS DE ASHINA",
        rallyQuote = "Hesitação é a derrota."
    ),
    DEFAULT(
        displayName = "Padrão",
        description = "Aço Escuro & Azul Astral",
        realmTitle = "SALÃO DOS DESAFIANTES",
        rallyQuote = "Levante-se e lute novamente."
    );

    companion object {
        val selectablePresets: List<GameThemePreset> = entries.filter { it != DEFAULT }

        fun fromGameName(gameName: String?): GameThemePreset {
            if (gameName.isNullOrBlank()) return DEFAULT
            val normalized = gameName.trim().lowercase()
            return when {
                normalized.contains("elden ring") || normalized == "elden" -> ELDEN_RING
                normalized.contains("phantom blade") || normalized == "phantom" -> PHANTOM_BLADE
                normalized.contains("dark souls iii") || normalized.contains("dark souls 3") -> DARK_SOULS_3
                normalized.contains("dark souls ii") || normalized.contains("dark souls 2") -> DARK_SOULS_2
                normalized.contains("dark soul") || normalized.contains("demon's souls") -> DARK_SOULS_1
                normalized.contains("bloodborne") -> BLOODBORNE
                normalized.contains("sekiro") -> SEKIRO
                else -> DEFAULT
            }
        }
    }
}

@Immutable
data class HudPalette(
    val preset: GameThemePreset,
    val accent: Color,
    val accentBright: Color,
    val onAccent: Color,
    val borderAccent: Color,
    val panelSurface: Color,
    val panelInnerSurface: Color,
    val buttonSurface: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val skyTop: Color,
    val skyMid: Color,
    val horizonGlow: Color,
    val groundBottom: Color
)

fun GameThemePreset.toHudPalette(): HudPalette = when (this) {
    GameThemePreset.ELDEN_RING -> HudPalette(
        preset = this,
        accent = EldenGoldPrimary,
        accentBright = EldenGoldBright,
        onAccent = EldenOnGold,
        borderAccent = EldenGoldBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = EldenSkyTop,
        skyMid = EldenSkyMid,
        horizonGlow = EldenHorizonGlow,
        groundBottom = EldenGroundBottom
    )
    GameThemePreset.PHANTOM_BLADE -> HudPalette(
        preset = this,
        accent = PhantomCrimsonPrimary,
        accentBright = PhantomCrimsonBright,
        onAccent = PhantomOnCrimson,
        borderAccent = PhantomCrimsonBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = PhantomSkyTop,
        skyMid = PhantomSkyMid,
        horizonGlow = PhantomHorizonGlow,
        groundBottom = PhantomGroundBottom
    )
    GameThemePreset.DARK_SOULS_1 -> HudPalette(
        preset = this,
        accent = SoulsEmberPrimary,
        accentBright = SoulsEmberBright,
        onAccent = SoulsOnEmber,
        borderAccent = SoulsEmberBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = SoulsSkyTop,
        skyMid = SoulsSkyMid,
        horizonGlow = SoulsHorizonGlow,
        groundBottom = SoulsGroundBottom
    )
    GameThemePreset.DARK_SOULS_2 -> HudPalette(
        preset = this,
        accent = Souls2EmberPrimary,
        accentBright = Souls2EmberBright,
        onAccent = Souls2OnEmber,
        borderAccent = Souls2EmberBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = Souls2SkyTop,
        skyMid = Souls2SkyMid,
        horizonGlow = Souls2HorizonGlow,
        groundBottom = Souls2GroundBottom
    )
    GameThemePreset.DARK_SOULS_3 -> HudPalette(
        preset = this,
        accent = Souls3EmberPrimary,
        accentBright = Souls3EmberBright,
        onAccent = Souls3OnEmber,
        borderAccent = Souls3EmberBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = Souls3SkyTop,
        skyMid = Souls3SkyMid,
        horizonGlow = Souls3HorizonGlow,
        groundBottom = Souls3GroundBottom
    )
    GameThemePreset.BLOODBORNE -> HudPalette(
        preset = this,
        accent = BloodbornePrimary,
        accentBright = BloodborneBright,
        onAccent = BloodborneOnPrimary,
        borderAccent = BloodborneBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = BloodborneSkyTop,
        skyMid = BloodborneSkyMid,
        horizonGlow = BloodborneHorizonGlow,
        groundBottom = BloodborneGroundBottom
    )
    GameThemePreset.SEKIRO -> HudPalette(
        preset = this,
        accent = SekiroPrimary,
        accentBright = SekiroBright,
        onAccent = SekiroOnPrimary,
        borderAccent = SekiroBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = SekiroSkyTop,
        skyMid = SekiroSkyMid,
        horizonGlow = SekiroHorizonGlow,
        groundBottom = SekiroGroundBottom
    )
    GameThemePreset.DEFAULT -> HudPalette(
        preset = this,
        accent = DefaultSteelPrimary,
        accentBright = DefaultSteelBright,
        onAccent = DefaultOnSteel,
        borderAccent = DefaultSteelBorder,
        panelSurface = HudPanelGlass,
        panelInnerSurface = HudPanelInnerGlass,
        buttonSurface = HudButtonDark,
        textPrimary = HudTextPrimary,
        textSecondary = HudTextSecondary,
        textMuted = HudTextMuted,
        skyTop = DefaultSkyTop,
        skyMid = DefaultSkyMid,
        horizonGlow = DefaultHorizonGlow,
        groundBottom = DefaultGroundBottom
    )
}

val LocalHudPalette = staticCompositionLocalOf { GameThemePreset.DEFAULT.toHudPalette() }

@Composable
fun ContadorBossFightTheme(
    currentGameName: String? = null,
    content: @Composable () -> Unit
) {
    val preset = GameThemePreset.fromGameName(currentGameName)
    val hudPalette = preset.toHudPalette()

    val colorScheme: ColorScheme = darkColorScheme(
        primary = hudPalette.accent,
        onPrimary = hudPalette.onAccent,
        primaryContainer = hudPalette.panelInnerSurface,
        onPrimaryContainer = hudPalette.accentBright,
        secondary = hudPalette.accentBright,
        onSecondary = hudPalette.onAccent,
        secondaryContainer = hudPalette.panelSurface,
        onSecondaryContainer = hudPalette.textPrimary,
        background = hudPalette.groundBottom,
        onBackground = hudPalette.textPrimary,
        surface = hudPalette.panelSurface,
        onSurface = hudPalette.textPrimary,
        surfaceVariant = hudPalette.panelInnerSurface,
        onSurfaceVariant = hudPalette.textSecondary,
        error = HudDangerRed
    )

    CompositionLocalProvider(LocalHudPalette provides hudPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
