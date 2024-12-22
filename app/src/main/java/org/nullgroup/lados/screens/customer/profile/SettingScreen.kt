package org.nullgroup.lados.screens.customer.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nullgroup.lados.compose.common.CustomExposedDropDownMenu
import org.nullgroup.lados.compose.common.ProfileTopAppBar
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.MenuItemsUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    themeSwitched: () -> Unit
) {
    var itemsUiState = MenuItemsUIState.Success(
        data = listOf("en", "vn")
    )

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            ProfileTopAppBar(onBackClick = { onBack() }, content = "Setting")
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding())
        ) {
            Text(
                text = "Regions",
                style = LadosTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LadosTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.small))
            CustomExposedDropDownMenu(itemsUiState = itemsUiState, placeHolder = "Language")

            Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.medium))
            Text(
                text = "Color Scheme",
                style = LadosTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LadosTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.small))
            ThemeModeCards(
                modifier = Modifier.fillMaxWidth(),
                onColorSelected = {
                    themeSwitched()
                }
            )
        }
    }
}

@Composable
fun ThemeModeCards(
    modifier: Modifier = Modifier,
    onColorSelected: () -> Unit = {}
) {
    var isLightMode by rememberSaveable {
        mutableStateOf(true)
    }
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Light Mode Card
        LadosTheme(darkTheme = false) {
            ThemeCard(
                isLightMode = isLightMode,
                modifier = Modifier.weight(1f),
                text = "Light mode",
                onColorSelected = {
                    isLightMode = isLightMode.not()
                    onColorSelected()
                }
            )
        }

        // Dark Mode Card
        LadosTheme(darkTheme = true) {
            ThemeCard(
                isLightMode = !isLightMode,
                modifier = Modifier.weight(1f),
                text = "Dark mode",
                onColorSelected = {
                    isLightMode = isLightMode.not()
                    onColorSelected()
                }
            )
        }
    }
}

@Composable
fun ThemeCard(
    isLightMode: Boolean,
    modifier: Modifier = Modifier,
    text: String,
    onColorSelected: () -> Unit = {}
) {
    val gradientColors =
        listOf(
            LadosTheme.colorScheme.background,
            LadosTheme.colorScheme.primaryContainer
        )

    val contentColor = LadosTheme.colorScheme.onBackground.copy(alpha = 0.8f)

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        onClick = onColorSelected
    ) {
        Box(
            modifier = Modifier
                .border(
                    BorderStroke(
                        if (isLightMode) 5.dp else 0.dp ,
                        LadosTheme.colorScheme.primary.copy(alpha = 0.5f)),
                    RoundedCornerShape(24.dp)
                )
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(gradientColors)
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Placeholder content bars
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                LadosTheme.colorScheme.primaryContainer
                                    .copy(alpha = 0.8f)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SettingScreenPreview() {
    Surface {
        ThemeModeCards()
    }
}