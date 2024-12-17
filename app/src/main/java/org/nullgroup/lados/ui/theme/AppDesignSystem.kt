package org.nullgroup.lados.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp


data class AppColorScheme(
    val background: Color,
    val onBackground: Color,
    val primary: Color,
    val onPrimary: Color,
    val secondary: Color,
    val onSecondary: Color,
    val error: Color,
    val onError: Color,
    val outline: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val onSurface: Color,
)

data class AppTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
)

data class AppShape(
    val none: Shape,
    val extraSmale: Shape,
    val small: Shape,
    val medium: Shape,
    val large: Shape,
    val extraLarge: Shape,
    val full: Shape,
)

data class AppSize(
    val small: Dp,
    val normal: Dp,
    val medium: Dp,
    val large: Dp,
)

val LocalAppColorScheme = staticCompositionLocalOf {
    AppColorScheme(
        background = Color.Unspecified,
        onBackground = Color.Unspecified,
        primary = Color.Unspecified,
        onPrimary = Color.Unspecified,
        secondary = Color.Unspecified,
        onSecondary = Color.Unspecified,
        error = Color.Unspecified,
        onError = Color.Unspecified,
        outline = Color.Unspecified,
        surfaceContainerLowest = Color.Unspecified,
        surfaceContainerLow = Color.Unspecified,
        surfaceContainer = Color.Unspecified,
        surfaceContainerHigh = Color.Unspecified,
        surfaceContainerHighest = Color.Unspecified,
        onSurface = Color.Unspecified,
    )
}

val LocalAppTypography = staticCompositionLocalOf {
    AppTypography(
        displayLarge = TextStyle.Default,
        displayMedium = TextStyle.Default,
        displaySmall = TextStyle.Default,
        headlineLarge = TextStyle.Default,
        headlineMedium = TextStyle.Default,
        headlineSmall = TextStyle.Default,
        titleLarge = TextStyle.Default,
        titleMedium = TextStyle.Default,
        titleSmall = TextStyle.Default,
        labelLarge = TextStyle.Default,
        labelMedium = TextStyle.Default,
        labelSmall = TextStyle.Default,
        bodyLarge = TextStyle.Default,
        bodyMedium = TextStyle.Default,
        bodySmall = TextStyle.Default,
    )
}

val LocalAppShape = staticCompositionLocalOf {
    AppShape(
        none = RectangleShape,
        extraSmale = RectangleShape,
        small = RectangleShape,
        medium = RectangleShape,
        large = RectangleShape,
        extraLarge = RectangleShape,
        full = RectangleShape,
    )
}

val LocalAppSize = staticCompositionLocalOf {
    AppSize(
        small = Dp.Unspecified,
        normal = Dp.Unspecified,
        medium = Dp.Unspecified,
        large = Dp.Unspecified,
    )
}