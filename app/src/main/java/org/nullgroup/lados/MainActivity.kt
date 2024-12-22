package org.nullgroup.lados

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Surface
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.RoleBasedNavigation
import org.nullgroup.lados.screens.common.SplashScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.common.ThemeViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT,
            ),
        )
        setContent {
            val themeViewModel = hiltViewModel<ThemeViewModel>()
            var isDarkTheme = themeViewModel.darkMode.collectAsState()
            LadosTheme(darkTheme = isDarkTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.ui.graphics.Color.Transparent
                ) {

                    var showSplash by remember { mutableStateOf(true) }

                    LaunchedEffect(Unit) {
                        delay(3000)
                        showSplash = false
                    }

                    if (showSplash) {
                        SplashScreen()
                    } else {
                        RoleBasedNavigation(
                            modifier = Modifier
                                .background(
                                    LadosTheme.colorScheme.background
                                ),
                            isDarkTheme = isDarkTheme.value,
                            themeSwitched = {
                                themeViewModel.modifyTheme()
                            }
                        )
                    }
                }
            }
        }
    }
}