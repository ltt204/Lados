package org.nullgroup.lados.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.nullgroup.lados.ui.theme.LadosTheme

@Preview(showBackground = true)
@Composable
fun LoadingScreen(modifier: Modifier = Modifier, circularSize: Int = 50) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                LadosTheme.colorScheme.background.copy(
                    alpha = 0.5f,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Dialog(onDismissRequest = {}) {
            CircularProgressIndicator(
                modifier = Modifier.size(circularSize.dp),
                color = LadosTheme.colorScheme.onBackground
            )
        }
    }
}