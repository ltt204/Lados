package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController
) {
//    Surface(modifier = modifier.padding(paddingValues).fillMaxSize()) {
//        Text(text = "Home Screen")
//    }
    CartScreen(innerPadding = paddingValues)
}