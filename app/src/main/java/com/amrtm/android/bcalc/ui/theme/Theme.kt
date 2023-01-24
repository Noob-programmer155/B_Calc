package com.amrtm.android.bcalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = primaryColDark,
    primaryVariant = primaryColVariantDark,
    secondary = secondaryColDark,
    secondaryVariant = secondaryColVariantDark,
    background = backgroundColDark,
    surface = backgroundColDark,
    error = errorColDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White,
)

private val LightColorPalette = lightColors(
    primary = primaryCol,
    primaryVariant = primaryColVariant,
    secondary = secondaryCol,
    secondaryVariant = secondaryColVariant,
    background = Color.White,
    surface = Color.White,
    error = errorCol,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = onBackgroundCol,
    onSurface = onBackgroundCol,
    onError = Color.White
)

@Composable
fun BCalcTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}