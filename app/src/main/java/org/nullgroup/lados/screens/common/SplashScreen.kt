package org.nullgroup.lados.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme as MaterialTheme3
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    // onNavigateToLogin: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme3.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Clot",
            style = MaterialTheme3.typography.displayLarge,
            color = MaterialTheme3.colorScheme.onPrimary
        )
    }

    LaunchedEffect(Unit) {
        delay(2000)
        // onNavigateToLogin()
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}