package io.github.carvalhoxx.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class GameThemePreset(
    val displayName: String,
    val description: String
) {
    ELDEN_RING("Elden Ring", "Dourado Escuro & Obsidiana"),
    PHANTOM_BLADE("Phantom Blade", "Vermelho Sangue & Cinza Escuro"),
    DARK_SOULS("Dark Souls", "Brasa da Fogueira & Cinzas"),
    BLOODBORNE("Bloodborne", "Sangue Gótico & Noite de Yharnam"),
    SEKIRO("Sekiro", "Vermelhão Shinobi & Carvão"),
    DEFAULT("Padrão", "Tema Azul Padrão");

    companion object {
        val selectablePresets: List<GameThemePreset> = entries.filter { it != DEFAULT }

        fun fromGameName(gameName: String?): GameThemePreset {
            if (gameName.isNullOrBlank()) return DEFAULT
            val normalized = gameName.trim().lowercase()
            return when {
                normalized.contains("elden ring") || normalized == "elden" -> ELDEN_RING
                normalized.contains("phantom blade") || normalized == "phantom" -> PHANTOM_BLADE
                normalized.contains("dark souls") || normalized.contains("demon's souls") -> DARK_SOULS
                normalized.contains("bloodborne") -> BLOODBORNE
                normalized.contains("sekiro") -> SEKIRO
                else -> DEFAULT
            }
        }
    }
}

private val DefaultDarkColorScheme = darkColorScheme(
    primary = DefaultBlue80,
    secondary = DefaultBlueGrey80,
    tertiary = DefaultCyan80
)

private val DefaultLightColorScheme = lightColorScheme(
    primary = DefaultBlue40,
    secondary = DefaultBlueGrey40,
    tertiary = DefaultCyan40
)

private val EldenRingColorScheme = darkColorScheme(
    primary = EldenGoldPrimary,
    onPrimary = Color(0xFF1F1600),
    primaryContainer = EldenGoldContainer,
    onPrimaryContainer = EldenOnGoldContainer,
    secondary = EldenGoldSecondary,
    onSecondary = Color(0xFF1F1808),
    secondaryContainer = Color(0xFF362C17),
    onSecondaryContainer = EldenGoldLight,
    tertiary = EldenAmberTertiary,
    background = EldenDarkBackground,
    onBackground = EldenOnSurface,
    surface = EldenDarkSurface,
    onSurface = EldenOnSurface,
    surfaceVariant = EldenDarkSurfaceVariant,
    onSurfaceVariant = EldenOnSurfaceVariant,
    surfaceContainer = EldenDarkSurfaceVariant,
    surfaceContainerHigh = Color(0xFF2E2A21),
    surfaceContainerLow = EldenDarkSurface
)

private val PhantomBladeColorScheme = darkColorScheme(
    primary = PhantomCrimsonPrimary,
    onPrimary = Color.White,
    primaryContainer = PhantomCrimsonContainer,
    onPrimaryContainer = PhantomOnCrimsonContainer,
    secondary = PhantomSteelSecondary,
    onSecondary = Color(0xFF1D1515),
    secondaryContainer = Color(0xFF3A2022),
    onSecondaryContainer = PhantomOnCrimsonContainer,
    tertiary = PhantomBladeTertiary,
    background = PhantomDarkBackground,
    onBackground = PhantomOnSurface,
    surface = PhantomDarkSurface,
    onSurface = PhantomOnSurface,
    surfaceVariant = PhantomDarkSurfaceVariant,
    onSurfaceVariant = PhantomOnSurfaceVariant,
    surfaceContainer = PhantomDarkSurfaceVariant,
    surfaceContainerHigh = Color(0xFF352E30),
    surfaceContainerLow = PhantomDarkSurface
)

private val DarkSoulsColorScheme = darkColorScheme(
    primary = SoulsEmberPrimary,
    onPrimary = Color(0xFF240C00),
    primaryContainer = SoulsEmberContainer,
    onPrimaryContainer = SoulsOnEmberContainer,
    secondary = SoulsAshSecondary,
    secondaryContainer = Color(0xFF382A21),
    onSecondaryContainer = SoulsOnEmberContainer,
    tertiary = SoulsFlameTertiary,
    background = SoulsDarkBackground,
    onBackground = Color(0xFFEDE0D8),
    surface = SoulsDarkSurface,
    onSurface = Color(0xFFEDE0D8),
    surfaceVariant = SoulsDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFD0C0B6),
    surfaceContainer = SoulsDarkSurfaceVariant,
    surfaceContainerHigh = Color(0xFF352E2A),
    surfaceContainerLow = SoulsDarkSurface
)

private val BloodborneColorScheme = darkColorScheme(
    primary = BloodbornePrimary,
    onPrimary = Color.White,
    primaryContainer = BloodborneContainer,
    onPrimaryContainer = BloodborneOnContainer,
    secondary = BloodborneSecondary,
    secondaryContainer = Color(0xFF332333),
    onSecondaryContainer = BloodborneOnContainer,
    tertiary = BloodborneTertiary,
    background = BloodborneBackground,
    onBackground = Color(0xFFEAE0EB),
    surface = BloodborneSurface,
    onSurface = Color(0xFFEAE0EB),
    surfaceVariant = BloodborneSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBC0CE),
    surfaceContainer = BloodborneSurfaceVariant,
    surfaceContainerHigh = Color(0xFF302938),
    surfaceContainerLow = BloodborneSurface
)

private val SekiroColorScheme = darkColorScheme(
    primary = SekiroPrimary,
    onPrimary = Color.White,
    primaryContainer = SekiroContainer,
    onPrimaryContainer = SekiroOnContainer,
    secondary = SekiroSecondary,
    secondaryContainer = Color(0xFF2D352D),
    onSecondaryContainer = SekiroOnContainer,
    tertiary = SekiroTertiary,
    background = SekiroBackground,
    onBackground = Color(0xFFE3E5E2),
    surface = SekiroSurface,
    onSurface = Color(0xFFE3E5E2),
    surfaceVariant = SekiroSurfaceVariant,
    onSurfaceVariant = Color(0xFFC2C7C1),
    surfaceContainer = SekiroSurfaceVariant,
    surfaceContainerHigh = Color(0xFF303530),
    surfaceContainerLow = SekiroSurface
)

@Composable
fun ContadorBossFightTheme(
    currentGameName: String? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val preset = GameThemePreset.fromGameName(currentGameName)

    val colorScheme: ColorScheme = when (preset) {
        GameThemePreset.ELDEN_RING -> EldenRingColorScheme
        GameThemePreset.PHANTOM_BLADE -> PhantomBladeColorScheme
        GameThemePreset.DARK_SOULS -> DarkSoulsColorScheme
        GameThemePreset.BLOODBORNE -> BloodborneColorScheme
        GameThemePreset.SEKIRO -> SekiroColorScheme
        GameThemePreset.DEFAULT -> when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DefaultDarkColorScheme
            else -> DefaultLightColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
