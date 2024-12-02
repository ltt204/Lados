package org.nullgroup.lados.compose.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TwoColsItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
    trailingAction: @Composable (onClick: () -> Unit) -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(modifier = modifier
        .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceBright,
        ),
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

        )
}