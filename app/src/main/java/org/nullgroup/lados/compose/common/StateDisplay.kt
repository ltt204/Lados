package org.nullgroup.lados.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun LoadOnProgress(
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit) = { },
) {
    Box(
        modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun LoadOnError(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { Text(text = "Failed to load data.") },
) {
    content()
}

@Composable
fun LoadOnSuccess(
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit) = { Text(text = "Failed to load data.") },
) {

}

