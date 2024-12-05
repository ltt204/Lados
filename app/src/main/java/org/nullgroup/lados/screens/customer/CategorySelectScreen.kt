package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import org.nullgroup.lados.R
import org.nullgroup.lados.data.models.Category
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.HomeViewModel
import org.nullgroup.lados.viewmodels.SharedViewModel

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
fun CategoryItemSelect(modifier: Modifier=Modifier, category: Category){

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GrayMaterial.copy(alpha = 0.2f))
            .fillMaxWidth()
            .height(72.dp)
            .padding(12.dp),

        ){
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically,

        ){
            SubcomposeAsyncImage(
                loading = {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                },
                model = ImageRequest
                    .Builder(context = LocalContext.current)
                    .data(category.categoryImage)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                error = {
                    Image(
                        painter = painterResource(R.drawable.placeholder_error_image),
                        contentDescription = "Error loading image")
                },
            )
            Text(
                text = category.categoryName,
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
fun DrawCategorySelectScreenContent(modifier: Modifier=Modifier, viewModel: HomeViewModel= hiltViewModel(), navController: NavController, paddingValues: PaddingValues, sharedViewModel: SharedViewModel=SharedViewModel()) {
    val categories = viewModel.categories.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Title(content = "Shop by Categories")
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(categories.value) { category ->
                CategoryItemSelect(category = category,
                    modifier = Modifier.clickable {
                        sharedViewModel.updateComplexData(category)
                        navController.navigate(Screen.Customer.DisplayProductInCategory.route)
                    })
            }
        }
    }
}

@Composable
fun CategorySelectScreen(modifier: Modifier=Modifier, navController: NavController, paddingValues: PaddingValues= PaddingValues(horizontal = 16.dp, vertical = 8.dp), sharedViewModel: SharedViewModel=SharedViewModel()){
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier= Modifier
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
                        .background(GrayMaterial.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Search",
                        tint = BlackMaterial

                    )
                }
            }
        }
    ) {
        DrawCategorySelectScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
            sharedViewModel = sharedViewModel
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Review() {
    LadosTheme {
        CategorySelectScreen(navController = NavController(LocalContext.current))
    }
}