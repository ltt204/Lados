package org.nullgroup.lados.compose.SignIn

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun EmailTextField(
    email: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value = email,
        onValueChange = onValueChange,
        label = {
            Text(
                text = "Email Address",
                style = LadosTheme.typography.bodyLarge,
                color = LadosTheme.colorScheme.onBackground
            )
        },
        placeholder = {
            Text(
                text="Email Address",
                style = LadosTheme.typography.titleMedium,
                color = LadosTheme.colorScheme.onBackground
            )
        },
        textStyle = LadosTheme.typography.titleMedium,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Email, contentDescription = "email")
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email, imeAction = ImeAction.Done
        ),
        isError = isError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = LadosTheme.colorScheme.surfaceContainerHighest,
            focusedBorderColor = LadosTheme.colorScheme.primary,
            unfocusedContainerColor = LadosTheme.colorScheme.surfaceContainerHigh,
            unfocusedBorderColor = Color.Transparent,
            errorBorderColor = LadosTheme.colorScheme.error,
            focusedTextColor = LadosTheme.colorScheme.onBackground,
            unfocusedTextColor = LadosTheme.colorScheme.onBackground,
            errorTextColor = LadosTheme.colorScheme.error,
        ),
    )
}