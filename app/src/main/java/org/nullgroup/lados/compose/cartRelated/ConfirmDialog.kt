package org.nullgroup.lados.compose.cartRelated

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

data class DialogInfo(
    val title: String,
    val message: String,
    val onConfirm: () -> Unit,
    val onCancel: (() -> Unit)? = null,
)

@Composable
fun ConfirmDialog(info: DialogInfo?) {
    if (info == null) {
        return
    }
    AlertDialog(
        title = {
            Text(text = info.title)
        },
        text = {
            Text(text = info.message)
        },
        onDismissRequest = {
            info.onCancel?.invoke()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    info.onConfirm.invoke()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    info.onCancel?.invoke()
                }
            ) {
                Text("Cancel")
            }
        }
    )
}