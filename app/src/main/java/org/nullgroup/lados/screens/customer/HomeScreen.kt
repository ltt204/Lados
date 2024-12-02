package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController? = null
) {
    Scaffold(
        modifier = modifier
            .padding(top = paddingValues.calculateTopPadding())
            .fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(Brush.radialGradient(
                    listOf(Color(0xFF2be4dc), Color(0xFF243484))
                ))
        ) {
            Text(text = "Home Screen")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}