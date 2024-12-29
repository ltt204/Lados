package org.nullgroup.lados.screens.customer.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.product.Title
import org.nullgroup.lados.ui.theme.LadosTheme

@Composable
fun DrawError_FindNotMatch(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(
                horizontal = 8.dp,
                vertical = paddingValues.calculateTopPadding()
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.searchbigicon),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Title(
            content = "Sorry, we couldn't find any matching results for your Search.", textStyle =
            TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                navController.navigate(
                    Screen.Customer.Home.route
                ) },
            // note: modify color
            colors = ButtonDefaults.buttonColors(LadosTheme.colorScheme.primary)
        ) {

            Text(text = "Explore Categories")


        }
    }
}

@Composable
fun Error_FindNotMatchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    Scaffold(
        modifier = modifier.padding(bottom = paddingValues.calculateBottomPadding()),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(CircleShape)
                        // note: modify color
                        .background(LadosTheme.colorScheme.secondary.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Search",
                        // note: modify color
                        tint = LadosTheme.colorScheme.onSurface

                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    navController = navController,
                    onSearch = {})
            }
        }
    ) {
        DrawError_FindNotMatch(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewError_FindNotMatch() {
    LadosTheme {
        Error_FindNotMatchScreen(navController = NavController(LocalContext.current))
    }
}