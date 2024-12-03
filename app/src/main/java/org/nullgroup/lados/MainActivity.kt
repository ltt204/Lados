package org.nullgroup.lados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.nullgroup.lados.navigations.CustomerGraph
import org.nullgroup.lados.navigations.RoleBasedNavigation
import org.nullgroup.lados.ui.theme.LadosTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LadosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    RoleBasedNavigation(
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    CustomerGraph(
                        modifier = Modifier.padding()
                    )
                }
            }
        }
    }
}