package org.nullgroup.lados.screens.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.signin.ButtonSubmit
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun NotifySendEmailScreen(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email Icon",
                    modifier = Modifier.size(60.dp),
                    tint = Color(0xFFFFB74D)
                )

                Canvas(modifier = Modifier) {
                    val path = Path().apply {
                        moveTo(-90f, -80f)
                        cubicTo(-200f, -140f, -100f, -250f, 0f, -250f)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFFFFB74D),
                        style = Stroke(
                            width = 10f,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                        )
                    )
                    val arrowHead = Path().apply {
                        moveTo(50f, -250f)
                        lineTo(0f, -280f)
                        lineTo(0f, -220f)
                        close()
                    }

                    drawPath(path = arrowHead, color = Color(0xFFFFB74D))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = LadosTheme.typography.headlineSmall,
            color = LadosTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(24.dp))

        ButtonSubmit(
            text = stringResource(R.string.auth_return_to_login),
            onClick = onClick,
        )
    }
}