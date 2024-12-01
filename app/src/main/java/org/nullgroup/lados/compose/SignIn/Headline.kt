package org.nullgroup.lados.compose.SignIn

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun Headline(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = LadosTheme.typography.headlineLarge.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = LadosTheme.colorScheme.onPrimary,
        modifier = modifier,
    )
}