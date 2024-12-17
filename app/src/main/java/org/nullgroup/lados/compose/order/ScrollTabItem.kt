package org.nullgroup.lados.compose.order

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun ScrollTabItem(
    modifier: Modifier = Modifier,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(34.dp)
            .clip(CircleShape),
        shape = CircleShape,
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                LadosTheme.colorScheme.outline
            else
                LadosTheme.colorScheme.surfaceContainerLowest,
        )
    ) {
        Text(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            text = title,
            fontSize = 16.sp,
            color = if (selected)
                LadosTheme.colorScheme.onSecondary
            else
                    LadosTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScrollTabItemPreview() {
    ScrollTabItem(
        title = "Mot cai chet truyen thong",
        selected = true,
        onClick = {}
    )
}