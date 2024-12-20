package org.nullgroup.lados.compose.cartRelated

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import org.nullgroup.lados.ui.theme.LadosTheme

/** A dialog that prompts the user to confirm an action.
 *
 * If the composable [title] or [message] is provided, it will be passed directly to the AlertDialog.
 * Otherwise, the [titleText] and [messageText] will be used to form the [Text] composable,
 * which will be passed to the AlertDialog.
 */
data class DialogInfo(
    val title: @Composable (() -> Unit)? = null,
    val titleText: String = "",
    val message: @Composable (() -> Unit)? = null,
    val messageText: String = "",
    val onConfirm: () -> Unit,
    val confirmText : String = "Confirm",
    val onCancel: (() -> Unit)? = null,
    val cancelText : String = "Cancel"
)

@Composable
fun ConfirmDialog(info: DialogInfo?) {
    if (info == null) {
        return
    }

    val titleLargeTypo = LadosTheme.typography.titleLarge
    val bodyMediumTypo = LadosTheme.typography.bodyMedium
    val labelLargeTypo = LadosTheme.typography.labelLarge

    val buttonColors = ButtonColors(
        contentColor = LadosTheme.colorScheme.onSurface,
        containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
        disabledContentColor = LadosTheme.colorScheme.onSurface,
        disabledContainerColor = LadosTheme.colorScheme.surfaceContainer,
    )

    AlertDialog(
        containerColor = LadosTheme.colorScheme.surfaceContainer,
        iconContentColor = LadosTheme.colorScheme.onSurface,
        titleContentColor = LadosTheme.colorScheme.onSurface,
        textContentColor = LadosTheme.colorScheme.onSurface,
        title = info.title ?:  {
            Text(
                text = info.titleText,
                style = titleLargeTypo.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = info.message ?: {
            Text(text = info.messageText, style = bodyMediumTypo)
        },
        onDismissRequest = {
            info.onCancel?.invoke()
        },
        confirmButton = {
            TextButton(
                colors = buttonColors,
                onClick = {
                    info.onConfirm.invoke()
                }
            ) {
                Text(info.confirmText, style = labelLargeTypo)
            }
        },
        dismissButton = if (info.onCancel == null) null else {{
            TextButton(
                colors = buttonColors,
                onClick = {
                    info.onCancel.invoke()
                }
            ) {
                Text(info.cancelText, style = labelLargeTypo)
            }}
        },
    )
}