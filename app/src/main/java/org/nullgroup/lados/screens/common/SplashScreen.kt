package org.nullgroup.lados.screens.common

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import org.nullgroup.lados.ui.theme.LadosTheme

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        val color = LadosTheme.colorScheme.primary.toArgb()
        val darkTheme: Boolean = isSystemInDarkTheme()

        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = color
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LadosTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Row() {
            Text(
                text = "C",
                style = LadosTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = LadosTheme.colorScheme.onPrimary,
                modifier = Modifier.offset(x = -10.dp, y = 5.dp)
            )
            Text(
                text = "l",
                style = LadosTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = LadosTheme.colorScheme.onPrimary,
                modifier = Modifier.offset(y = -5.dp),
            )
            Text(
                text = "o",
                style = LadosTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = LadosTheme.colorScheme.onPrimary,
                modifier = Modifier.offset(x = 10.dp, y = 5.dp)
            )
            Text(
                text = "t",
                style = LadosTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                color = LadosTheme.colorScheme.onPrimary,
                modifier = Modifier.offset(x = 20.dp, y = -5.dp),
            )
        }
    }
}
