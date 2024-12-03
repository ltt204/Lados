package org.nullgroup.lados.compose.SignIn

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.events.LoginScreenEvent

@Composable
fun ButtonSubmit(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = LadosTheme.shape.extraLarge,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = LadosTheme.colorScheme.primary,
        contentColor = LadosTheme.colorScheme.onPrimary,
    ),
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = colors,
        shape = shape,
    ) {
        Text(
            text = text,
            style = LadosTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}