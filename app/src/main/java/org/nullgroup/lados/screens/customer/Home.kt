package org.nullgroup.lados.screens.customer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import org.nullgroup.lados.R
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.BrownMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.WhiteMaterial
import org.nullgroup.lados.viewmodels.HomeViewModel

@Composable
fun SearchAndFilter(modifier: Modifier=Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SearchBar()
        Spacer(Modifier.width(10.dp))
        FilterButton()
    }

}

@Composable
fun SearchBar(modifier: Modifier=Modifier) {
    OutlinedTextField(
        value = "", // Set initial value to empty for placeholder to be visible
        onValueChange = {},
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp)),
        singleLine = true,
        placeholder = { Text("Search") }, // Add placeholder
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search, // Replace with your desired icon
                contentDescription = "Search"
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedBorderColor = GrayMaterial,
            focusedBorderColor = BrownMaterial
        )
    )
}


@Composable
fun FilterButton(modifier: Modifier=Modifier/*, onClick: () -> Unit*/) {
    Button(
        onClick = { /*onClick()*/ },
        contentPadding = PaddingValues(4.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            BrownMaterial
        ),
        modifier = modifier
            .size(48.dp)
        ,
        shape = RoundedCornerShape(8.dp),
    )
    {
        Icon(
            Icons.Filled.Menu,
            contentDescription = "Filter",
            tint = WhiteMaterial
        )
    }
}

@Composable
fun HeaderBanner(modifier: Modifier=Modifier){
    Image(painter = painterResource(R.drawable.ic_launcher_background), contentDescription = null)
}

@Composable
fun CategoryCircle(modifier: Modifier=Modifier, @DrawableRes image: Int) {
    Box(modifier
        .clip(CircleShape)
        .background(BrownMaterial.copy(alpha = 0.2f))
        .padding(0.dp)
        .size(64.dp)
    ){
        Image(
            painter = painterResource(image), contentDescription = null,
            modifier = modifier
                .padding(12.dp)
        )
    }
}

@Composable
fun CategoryTextRow(modifier: Modifier=Modifier) {
    Row(
        modifier = modifier
    ) {
        Text(text="Categogy",
            style = TextStyle(
                fontSize = 24.sp,
                color = BlackMaterial,
            )
        )
        Spacer(Modifier.weight(1f))
        LinkText("See all", "https://google.com", BrownMaterial, SpanStyle(color= BrownMaterial))
    }
}

@Composable
fun Category(modifier: Modifier=Modifier) {

    val homeViewModel: HomeViewModel = hiltViewModel()

    val categories = homeViewModel.categories.collectAsStateWithLifecycle()
    Log.d("CategoryRepositoryImplement", "getAllCategoriesFromFireStore: $categories")

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(categories.value)
        { category ->
            CategoryItem(image = R.drawable.ic_launcher_background, label = category.categoryName)
        }
    }

}

@Composable
fun LinkText(content: String? = null, url: String, color: Color, style: SpanStyle) {
    Text(
        buildAnnotatedString {
            withLink(
                LinkAnnotation.Url(
                    url,
                    TextLinkStyles(style = style)
                )
            ) {
                append(content)
            }
        }
    )
}

@Composable
fun CategoryItem(modifier: Modifier=Modifier, @DrawableRes image: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        CategoryCircle(image = image)
        Spacer(Modifier.height(8.dp))
        Text(text = label, color = BlackMaterial)
    }
}

@Composable
fun FlashSaleTitle(modifier: Modifier=Modifier) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = "Flash Sale",
            style = TextStyle(
                fontSize = 24.sp,
                color = BlackMaterial,

                )
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "Closing in : ",
            style = TextStyle(
                fontSize = 16.sp,
                color = BlackMaterial,
                fontWeight = FontWeight.Thin,
            ),
        )
        ComboTimeBox(hh=12,mm=4,ss=3)
    }
}

@Composable
fun TimerBox(modifier: Modifier=Modifier, content: String) {
    Box(

        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(BrownMaterial.copy(alpha = 0.3f))
            .size(24.dp)

    ) {
        Text(
            text = content,
            style = TextStyle(
                fontSize = 16.sp,
                color = BrownMaterial,
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ComboTimeBox(modifier: Modifier=Modifier, hh: Int, mm: Int, ss: Int) {

    val hhStr = hh.toString().padStart(2, '0')
    val mmStr = mm.toString().padStart(2, '0')
    val ssStr = ss.toString().padStart(2, '0')
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,

        ) {
        TimerBox(content = hhStr)
        Text(text=":",
            style = TextStyle(
                color = BrownMaterial
            ))
        TimerBox(content = mmStr)
        Text(text=":",
            style = TextStyle(
                color = BrownMaterial
            ))
        TimerBox(content = ssStr)
    }
}
@Composable
fun NormalTextField(
    label: String,
    modifier: Modifier = Modifier,
    Icon: @Composable (() -> Unit)
) {
    val (text, setText) = mutableStateOf("")
    TextField(
        leadingIcon = Icon,
        value = text,
        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent),
        onValueChange = setText,
        label = { Text(text = label, color = GrayMaterial)},
        modifier = modifier

    )
}

@Composable
fun CategoryFilter(modifier: Modifier=Modifier){
    LazyRow(
        modifier=modifier,

        horizontalArrangement = Arrangement.spacedBy(2.dp),
        contentPadding= PaddingValues(4.dp),
    ){
        items(20)
        {
                item ->
            CategoryBox(content="Category $item")
        }
    }
}


@Composable
fun CategoryFilterItem(modifier: Modifier=Modifier, content: String) {

    LazyVerticalGrid(
        modifier = modifier
            .heightIn(max=1000.dp),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(4.dp),

        ) {
        items(20) { item ->
            Box(
                modifier = modifier
                    .background(BrownMaterial)
                    .height(120.dp)
            ) {

            }
        }
    }
}

@Composable
fun CategoryBox(modifier: Modifier=Modifier, content: String) {
    val isChoose by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .border(
                BorderStroke(
                    1.dp,
                    GrayMaterial.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)


    ) {
        Text(
            text = content,
            style = TextStyle(
                fontSize = 16.sp,
                color = BlackMaterial,
            ),

            )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun AutoSlidingImageSlider(images: List<Int>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState()

    // Automatically slide to the next page
    LaunchedEffect(pagerState) {
        while (true) {
            yield() // Allow Compose to stay responsive
            delay(3000L) // Delay for 3 seconds
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp) // Slider height
    ) {
        // Pager for sliding images
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Image $page",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Adjust image scaling
            )
        }

        // Dots Indicator
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            activeColor = Color.White,
            inactiveColor = Color.Gray,
            indicatorWidth = 8.dp,
            indicatorHeight = 8.dp,
            spacing = 4.dp
        )
    }
}

@Composable
fun BannerSlider() {
    val images = listOf(
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background,
        R.drawable.ic_launcher_background
    )

    Column(
        modifier = Modifier

            .padding(16.dp)
    ) {
        AutoSlidingImageSlider(images = images)
    }
}


@Composable
fun ProductScreen(modifier: Modifier = Modifier,
                  paddingValues: PaddingValues = PaddingValues(0.dp),
                  navController: NavController
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),

            ) {
            item {
                SearchAndFilter()
            }
            item {
                BannerSlider()
            }
            item {
                CategoryTextRow()
            }
            item {
                Category()
            }
            item {
                FlashSaleTitle()
            }
            item {
                CategoryFilter()
                Spacer(Modifier.height(8.dp))
            }

            item {

                CategoryFilterItem(content = "ABC")

            }

        }


    }
}

/*
@Preview(name="Search And Filter Review", showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview2() {
    LadosTheme {
        //SearchAndFilter()
        //HeaderBanner()
        //CategoryItem(image=R.drawable.ic_launcher_background, lable="Test")
        //CategoryTextRow()
        //Category()
        //LinkText("ABC", "https://google.com", BrownMaterial, SpanStyle(color= BrownMaterial, textDecoration = TextDecoration.Underline))
        //TimerBox(content="12")
        //FlashSaleTitle()
        //CategoryBox(content="Man")
        //CategoryFilter()
        //CategoryFilterItem(content="ABC")
        //BannerSlider()
    }
}
*/

//@Preview(name="Summary", showBackground = true, showSystemUi = true)
//@Composable
//fun Summary()
//{
//    LadosTheme {
//        ProductScreen()
//    }
//}

