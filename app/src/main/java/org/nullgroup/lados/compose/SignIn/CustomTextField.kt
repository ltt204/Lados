package org.nullgroup.lados.compose.SignIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun CustomTextField(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = LadosTheme.shape.medium,
    leadingIcon: @Composable() (() -> Unit)? = null,
    trailingIcon: @Composable() (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    label: String = "",
) {
    OutlinedTextField(
        value = text,
        onValueChange = onValueChange,
        shape = shape,
        placeholder = {
            Text(
                text = label,
                color = LadosTheme.colorScheme.onBackground,
                style = LadosTheme.typography.bodyMedium
            )
        },
        textStyle = LadosTheme.typography.titleMedium,
        modifier = modifier,
        singleLine = true,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
        visualTransformation = visualTransformation,
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