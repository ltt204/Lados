package org.nullgroup.lados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.nullgroup.lados.data.models.UserRole
import org.nullgroup.lados.navigations.RoleBasedNavigation
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.HomeScreenViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LadosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RoleBasedNavigation(userRole = UserRole.ADMIN, modifier = Modifier.padding(innerPadding))
                }

            }
        }
    }
}
