package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.nullgroup.lados.screens.Screen

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    navController: NavController
) {
//    Surface(modifier = modifier.padding(paddingValues).fillMaxSize()) {
//        Text(text = "Home Screen")
//    }
    Button(
        onClick = {
            navController.navigate(Screen.Customer.CartScreen.route)
        },
        modifier = modifier.padding(paddingValues),
    ) {
        Image(Icons.Default.ShoppingCart, contentDescription = "Cart")
    }
}