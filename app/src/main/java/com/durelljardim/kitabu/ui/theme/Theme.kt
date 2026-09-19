package com.durelljardim.kitabu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = InkBlue,
    onPrimary = PaperWhite,
    primaryContainer = InkBlueSoft,
    onPrimaryContainer = InkBlue,
    secondary = InkBlue,
    onSecondary = PaperWhite,
    secondaryContainer = InkBlueSoft,
    onSecondaryContainer = InkBlue,
    background = Paper,
    onBackground = InkText,
    surface = Paper,
    onSurface = InkText,
    surfaceVariant = PaperDark,
    onSurfaceVariant = MutedText,
    surfaceTint = Paper,
    outline = Hairline,
    outlineVariant = HairlineLight,
    surfaceContainerLowest = ContainerLowest,
    surfaceContainerLow = ContainerLow,
    surfaceContainer = Container,
    surfaceContainerHigh = ContainerHigh,
    surfaceContainerHighest = HairlineLight
)

private val DarkColorScheme = darkColorScheme(
    primary = InkBlueDark,
    onPrimary = InkBlueOnDark,
    primaryContainer = InkBlueContainerDark,
    onPrimaryContainer = InkBlueSoft,
    secondary = InkBlueDark,
    onSecondary = InkBlueOnDark,
    secondaryContainer = InkBlueContainerDark,
    onSecondaryContainer = InkBlueSoft,
    background = NightPaper,
    onBackground = NightText,
    surface = NightPaper,
    onSurface = NightText,
    surfaceVariant = NightPaperDark,
    onSurfaceVariant = MutedTextDark,
    surfaceTint = NightPaper,
    outline = HairlineDark,
    outlineVariant = HairlineLightDark,
    surfaceContainerLowest = ContainerLowestDark,
    surfaceContainerLow = ContainerLowDark,
    surfaceContainer = ContainerDark,
    surfaceContainerHigh = ContainerHighDark,
    surfaceContainerHighest = HairlineLightDark
)

@Composable
fun KitabuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
