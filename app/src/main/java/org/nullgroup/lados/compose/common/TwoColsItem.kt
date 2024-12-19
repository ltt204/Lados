package org.nullgroup.lados.compose.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun TwoColsItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
    trailingAction: @Composable (onClick: () -> Unit) -> Unit = {},
    onClick: () -> Unit = {},
    colors: CardColors = CardDefaults.cardColors(
        containerColor = LadosTheme.colorScheme.surfaceContainerHigh,
        contentColor = LadosTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = LadosTheme.colorScheme.outline,
    )
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .heightIn(min = 64.dp, max = 128.dp),
        colors = colors,
        onClick = { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
            trailingAction { onClick() }
        }
    }
}

@Preview
@Composable
fun ProfileItemPreview() {
    TwoColsItem(
        content = { Text(text = "Test") },
        trailingAction = { Text(modifier = Modifier.fillMaxHeight(), text = "Action") }
    )
}