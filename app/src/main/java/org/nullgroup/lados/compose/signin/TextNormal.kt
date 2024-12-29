package org.nullgroup.lados.compose.signin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun TextNormal(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = LadosTheme.typography.bodyLarge,
        color = LadosTheme.colorScheme.onBackground,
        modifier = modifier,
    )
}