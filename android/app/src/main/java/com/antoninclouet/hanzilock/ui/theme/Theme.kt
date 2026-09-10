package com.antoninclouet.hanzilock.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

/** Tokens de la charte HanziLock (sceau / palette / typographie). */
object Brand {
    val paper = Color(0xFFEDE7DC)
    val paperSoft = Color(0xFFF6F2E9)
    val ink = Color(0xFF1C1A17)
    val inkSoft = Color(0xFF5B564C)
    val seal = Color(0xFFB7301F)
    val sealPressed = Color(0xFF8C2216)
    val jade = Color(0xFF2F6F5E)

    val hanziFamily = FontFamily.Serif
}

private val LightColors = lightColorScheme(
    primary = Brand.seal,
    onPrimary = Brand.paper,
    secondary = Brand.jade,
    background = Brand.paper,
    onBackground = Brand.ink,
    surface = Brand.paperSoft,
    onSurface = Brand.ink,
    surfaceVariant = Brand.paperSoft,
    onSurfaceVariant = Brand.inkSoft,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE2503C),
    onPrimary = Brand.ink,
    secondary = Brand.jade,
    background = Brand.ink,
    onBackground = Brand.paper,
    surface = Color(0xFF262320),
    onSurface = Brand.paper,
    surfaceVariant = Color(0xFF262320),
    onSurfaceVariant = Color(0xFFB8B2A6),
)

private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
    ),
)

@Composable
fun HanziLockTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
    MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
}
