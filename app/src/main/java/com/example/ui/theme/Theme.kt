package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class AccentOption(val key: String, val label: String, val primary: Color, val secondary: Color) {
    CYAN("cyan", "Electric Cyan", ElectricCyan, DeepBlue600),
    INDIGO("indigo", "Royal Indigo", RoyalIndigo, ElectricCyanSoft),
    EMERALD("emerald", "Emerald Mint", EmeraldMint, ElectricCyan),
    AMBER("amber", "Sunset Gold", GoldAmber, SunsetCoral),
}

enum class SeasonalTheme(val key: String, val labelBn: String, val labelEn: String) {
    NONE("none", "ডিফল্ট টেক", "Default Tech"),
    EID("eid", "ঈদ ও রমজান", "Ramadan & Eid"),
    BOISHAKH("boishakh", "পহেলা বৈশাখ", "Pohela Boishakh"),
}

private val AppShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun KabirTechLabTheme(
    themeMode: String = "dark",
    accentKey: String = "cyan",
    seasonalKey: String = "none",
    fontScale: Float = 1.0f,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themeMode) {
        "light" -> false
        "system" -> systemDark
        else -> true
    }
    val isAmoled = themeMode == "amoled"

    val accent = AccentOption.entries.firstOrNull { it.key == accentKey } ?: AccentOption.CYAN
    val primaryColor = when (seasonalKey) {
        "eid" -> GoldAmber
        "boishakh" -> RoseCrimson
        else -> accent.primary
    }

    val colorScheme = if (useDark) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = DeepBlue950,
            primaryContainer = DeepBlue700,
            onPrimaryContainer = TextPrimaryDark,
            secondary = accent.secondary,
            onSecondary = DeepBlue950,
            secondaryContainer = DeepBlue800,
            onSecondaryContainer = ElectricCyanSoft,
            tertiary = GoldAmber,
            onTertiary = DeepBlue950,
            background = if (isAmoled) Color.Black else DeepBlue900,
            onBackground = if (highContrast) Color.White else TextPrimaryDark,
            surface = if (isAmoled) Color(0xFF090D16) else DeepBlue850,
            onSurface = if (highContrast) Color.White else TextPrimaryDark,
            surfaceVariant = if (isAmoled) Color(0xFF101726) else DeepBlue800,
            onSurfaceVariant = if (highContrast) Color(0xFFE2E8F0) else TextSecondaryDark,
            outline = if (highContrast) ElectricCyan else OutlineDark,
            error = RoseCrimson,
            onError = Color.White,
        )
    } else {
        lightColorScheme(
            primary = DeepBlue600,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDBEAFE),
            onPrimaryContainer = DeepBlue900,
            secondary = ElectricCyanMuted,
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE0F2FE),
            onSecondaryContainer = DeepBlue900,
            tertiary = SunsetCoral,
            onTertiary = Color.White,
            background = LightBackground,
            onBackground = if (highContrast) Color.Black else TextPrimaryLight,
            surface = LightSurface,
            onSurface = if (highContrast) Color.Black else TextPrimaryLight,
            surfaceVariant = LightSurfaceVariant,
            onSurfaceVariant = TextSecondaryLight,
            outline = if (highContrast) DeepBlue700 else OutlineLight,
            error = RoseCrimson,
            onError = Color.White,
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = buildAppTypography(fontScale),
        shapes = AppShapes,
        content = content
    )
}
