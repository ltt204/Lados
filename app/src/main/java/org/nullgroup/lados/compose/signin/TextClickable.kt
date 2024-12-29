package org.nullgroup.lados.compose.signin

import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun TextClickable(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = LadosTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Bold,
        ),
        color = LadosTheme.colorScheme.onBackground,
        modifier = modifier.clickable {
            onClick()
        }
    )
}