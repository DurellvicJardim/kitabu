package com.durelljardim.kitabu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BurntOrange,
    onPrimary = Color.White,
    primaryContainer = LightGrey,
    onPrimaryContainer = LightText,
    secondary = HeaderBlack,
    onSecondary = Color.White,
    secondaryContainer = LightGrey,
    onSecondaryContainer = LightText,
    background = LightBackground,
    onBackground = LightText,
    surface = LightBackground,
    onSurface = LightText,
    surfaceVariant = LightGrey,
    onSurfaceVariant = LightMutedText,
    surfaceTint = LightBackground,
    error = LightError,
    onError = Color.White,
    outline = LightOutline,
    outlineVariant = LightOutline,
    inverseSurface = HeaderBlack,
    inverseOnSurface = Color.White,
    surfaceContainerLowest = LightBackground,
    surfaceContainerLow = LightBackground,
    surfaceContainer = LightBackground,
    surfaceContainerHigh = LightBackground,
    surfaceContainerHighest = LightGrey
)

private val DarkColorScheme = darkColorScheme(
    primary = BurntOrange,
    onPrimary = Color.White,
    primaryContainer = DarkGrey,
    onPrimaryContainer = DarkText,
    secondary = DarkText,
    onSecondary = DarkBackground,
    secondaryContainer = DarkGrey,
    onSecondaryContainer = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkBackground,
    onSurface = DarkText,
    surfaceVariant = DarkGrey,
    onSurfaceVariant = DarkMutedText,
    surfaceTint = DarkBackground,
    error = DarkError,
    onError = DarkBackground,
    outline = DarkOutline,
    outlineVariant = DarkOutline,
    inverseSurface = DarkText,
    inverseOnSurface = DarkBackground,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkBackground,
    surfaceContainer = DarkBackground,
    surfaceContainerHigh = DarkGrey,
    surfaceContainerHighest = DarkGrey
)

private val SquareCorner = RoundedCornerShape(0.dp)

private val SquareShapes = Shapes(
    extraSmall = SquareCorner,
    small = SquareCorner,
    medium = SquareCorner,
    large = SquareCorner,
    extraLarge = SquareCorner
)

@Composable
fun KitabuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = SquareShapes,
        content = content
    )
}
