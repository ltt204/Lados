package org.nullgroup.lados.screens.customer.profile

import android.app.Activity
import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.CustomExposedDropDownMenu
import org.nullgroup.lados.compose.common.DefaultCenterTopAppBar
import org.nullgroup.lados.compose.profile.ConfirmDialog
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.SupportedRegion
import org.nullgroup.lados.utilities.capitalizeWords
import org.nullgroup.lados.utilities.updateLocale
import org.nullgroup.lados.viewmodels.customer.home.HomeViewModel
import org.nullgroup.lados.viewmodels.common.SettingViewModel
import org.nullgroup.lados.viewmodels.customer.profile.address.MenuItemsUIState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController,
    themeSwitched: () -> Unit,
    settingViewModel: SettingViewModel = hiltViewModel(),
) {
    var isRegionChanged by remember {
        mutableStateOf(false)
    }

    val itemsUiState = MenuItemsUIState.Success(
        data = SupportedRegion.entries.map { it.regionName.capitalizeWords() },
    )
    val context = LocalContext.current
    val currentRegion = settingViewModel.locale.collectAsState().value
    var region by remember {
        mutableStateOf(currentRegion.locale)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            DefaultCenterTopAppBar(
                onBackClick = { onBack() },
                content = stringResource(R.string.profile_setting)
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(top = innerPadding.calculateTopPadding())
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_setting_regions),
                style = LadosTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = LadosTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.small))
            CustomExposedDropDownMenu(
                itemsUiState = itemsUiState,
                placeHolder = "Language",
                onItemSelected = { _, index ->
                    val locale = SupportedRegion.entries[index].locale
                    isRegionChanged = isRegionChanged.not()
                    region = locale
                },
                currentItem = currentRegion.regionName.capitalizeWords(),
            )

            Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.medium))
            Text(
                text = stringResource(R.string.profile_setting_color_scheme),
                style = LadosTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                color = LadosTheme.colorScheme.onBackground,
            )
            Spacer(modifier = Modifier.padding(bottom = LadosTheme.size.small))
            ThemeModeCards(
                modifier = Modifier.fillMaxWidth(),
                onColorSelected = {
                    themeSwitched()
                },
                isDarkMode = settingViewModel.darkMode.collectAsState().value
            )
        }
    }

    if (isRegionChanged) {
        ConfirmDialog(
            title = { Text(text = stringResource(
                R.string.dialog_title_region_change,
                region.country.capitalizeWords()
            )) },
            message = { Text(text = stringResource(R.string.dialog_message_region_change)) },
            onDismissRequest = {
                isRegionChanged = isRegionChanged.not()
                region = currentRegion.locale
            },
            confirmButton = {
                Log.d("SettingScreen", "locale: ${region}")
                isRegionChanged = isRegionChanged.not()
                settingViewModel.saveLocale(region)
                updateLocale(context, region)
                navController.clearBackStack(Screen.Customer.Home.route)
                navController.navigate(Screen.Customer.Home.route)
                (context as? Activity)?.recreate()
            },
            primaryButtonText = stringResource(R.string.dialog_agree),
            secondaryButtonText = stringResource(R.string.dialog_cancel)
        )
    }
}

@Composable
fun ThemeModeCards(
    modifier: Modifier = Modifier,
    onColorSelected: () -> Unit = {},
    isDarkMode: Boolean
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Light Mode Card
        LadosTheme(darkTheme = false) {
            ThemeCard(
                isDarkMode = isDarkMode,
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.setting_light_mode),
                onColorSelected = {
                    onColorSelected()
                }
            )
        }

        // Dark Mode Card
        LadosTheme(darkTheme = true) {
            ThemeCard(
                isDarkMode = !isDarkMode,
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.setting_dark_mode),
                onColorSelected = {
                    onColorSelected()
                }
            )
        }
    }
}

@Composable
fun ThemeCard(
    isDarkMode: Boolean,
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
        onClick = onColorSelected,
        enabled = isDarkMode
    ) {
        Box(
            modifier = Modifier
                .border(
                    BorderStroke(
                        if (!isDarkMode) 5.dp else 0.dp,
                        LadosTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
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
        ThemeModeCards(
            isDarkMode = false
        )
    }
}