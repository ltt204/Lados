package org.nullgroup.lados.screens.customer

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.nullgroup.lados.R
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.BrownMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.WhiteMaterial
import java.util.Calendar

@Composable
fun Title(modifier: Modifier=Modifier, textStyle: TextStyle=TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight.Bold,
    color = BlackMaterial,
), content: String) {
    Text(
        text = content,
        style = textStyle
    )
}

@Composable
fun CategoryItemSelect(modifier: Modifier=Modifier, @DrawableRes image: Int, label: String){
    Box(modifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .background(GrayMaterial.copy(alpha=0.2f))
        .fillMaxWidth()
        .height(72.dp)
        .padding(8.dp),

    ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,

        ){
            Image(
                painter = painterResource(image), contentDescription = null,
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BlackMaterial,
                )
            )
        }
    }
}

@Composable
fun DrawCategorySelectScreen(modifier: Modifier=Modifier, navController: NavController) {
    Column (
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ){
        Title(content = "Shop by Categories")
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(20) { item ->
                CategoryItemSelect(image = R.drawable.ic_launcher_background, label = "Category $item")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Review() {
    LadosTheme {
        DrawCategorySelectScreen(navController = NavController(LocalContext.current))
    }
}