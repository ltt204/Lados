package org.nullgroup.lados.compose.profile

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ConfirmDialog(
    title: @Composable (() -> Unit) = {},
    message: @Composable (() -> Unit) = {},
    onDismissRequest: () -> Unit = {},
    confirmButton: () -> Unit = {}
) {
    AlertDialog(
        title = { title() },
        text = { message() },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton(onClick = { confirmButton() }) {
                Text(text = "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = "Cancel")
            }
        })
}