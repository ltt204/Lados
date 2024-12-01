package org.nullgroup.lados.screens.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nullgroup.lados.ui.theme.Typography

@Preview
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xff8e6cef)),
        contentAlignment = Alignment.Center
    ) {
        Row() {
            Text(
                text = "C",
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.offset(x = -10.dp, y = 5.dp)
            )
            Text(
                text = "l",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 96.sp
                ),
                modifier = Modifier.offset(y = -5.dp),
            )
            Text(
                text = "o",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 96.sp
                ),
                modifier = Modifier.offset(x = 10.dp, y = 5.dp)
            )
            Text(
                text = "t",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 96.sp
                ),
                modifier = Modifier.offset(x = 20.dp, y = -5.dp),
            )
        }

    }
}