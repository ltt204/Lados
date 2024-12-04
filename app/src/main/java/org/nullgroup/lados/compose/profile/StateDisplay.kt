package org.nullgroup.lados.compose.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoadOnProgress(
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit) = {  }
) {
    Box(
        modifier
            .fillMaxSize()
            .background(Color.Transparent.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier
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
    content: @Composable (() -> Unit) = { Text(text = "Failed to load data.") }
) {
    Text(text = "Failed to load data.")
}

@Composable
fun LoadOnSuccess(
    modifier: Modifier = Modifier,
    content: @Composable (() -> Unit) = { Text(text = "Failed to load data.") }
) {

}

