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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import org.nullgroup.lados.ui.theme.MagentaMaterial
import org.nullgroup.lados.ui.theme.WhiteMaterial
import java.util.Calendar

@Composable
fun SearchBarRow(modifier: Modifier=Modifier, navController: NavController) {
    Row(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .height(56.dp)
            .fillMaxWidth()
        ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        SearchBar(navController = navController, onSearch = {})
    }

}

@Composable
fun SearchBar(modifier: Modifier=Modifier, navController: NavController, onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(modifier = modifier.fillMaxWidth()
        .clickable { navController.navigate(Screen.Customer.SearchScreen.route) },
        contentAlignment = Alignment.Center
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {},
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    BlackMaterial,
                    shape = RoundedCornerShape(50)
                )
                .align(Alignment.Center)

            ,

            singleLine = true,
            placeholder = { Text("Search") },
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.searchicon),
                    contentDescription = "Search",
                    modifier = Modifier.size(20.dp)
                )
            },

            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedBorderColor = GrayMaterial,
                focusedBorderColor = BrownMaterial
            )
        )
    }
}

@Composable
fun FilterButton(modifier: Modifier=Modifier, navController: NavController/*, onClick: () -> Unit*/) {
    Button(
        onClick = { navController.navigate(Screen.Customer.FilterScreen.route) },
        contentPadding = PaddingValues(2.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            BrownMaterial
        ),
        modifier = modifier
            .size(48.dp),
        shape = RoundedCornerShape(8.dp),
    )
    {
        Icon(
            Icons.Filled.Menu,
            modifier= Modifier
                .fillMaxSize()
                .padding(8.dp)
            ,
            contentDescription = "Filter",
            tint = WhiteMaterial
        )
    }
}

@Composable
fun CategoryCircle(modifier: Modifier=Modifier, @DrawableRes image: Int) {
    Box(
        modifier
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
fun TitleTextRow(modifier: Modifier=Modifier, content: String, color: Color = BlackMaterial) {
    Row(
        modifier = modifier
    ) {
        Text(text=content,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        )
        Spacer(Modifier.weight(1f))
        LinkText("See all", "https://google.com", BrownMaterial, SpanStyle(color= BrownMaterial, fontSize = 20.sp))
    }
}

@Composable
fun Category(modifier: Modifier=Modifier) {
/*
    val homeViewModel: HomeViewModel = hiltViewModel()

    val categories = homeViewModel.categories.collectAsStateWithLifecycle()
    Log.d("CategoryRepositoryImplement", "getAllCategoriesFromFireStore: $categories")
*/
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(20)
        { category ->
            CategoryItem(image = R.drawable.ic_launcher_background, label = category.toString())
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
        ComboTimeBox()
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
fun ComboTimeBox(modifier: Modifier=Modifier) {
    val coroutineScope = rememberCoroutineScope()
    var currentTime by remember { mutableStateOf(Calendar.getInstance()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Calendar.getInstance()
            delay(1000)
        }
    }

    val hh = currentTime.get(Calendar.HOUR_OF_DAY)
    val mm = currentTime.get(Calendar.MINUTE)
    val ss = currentTime.get(Calendar.SECOND)

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
fun CategoryFilter(modifier: Modifier=Modifier){
    LazyRow(
        modifier=modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding= PaddingValues(4.dp),
    ){
        items(5)
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
    LaunchedEffect(pagerState) {
        while (true) {
            yield()
            delay(3000L)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Image $page",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

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
fun ProductItem(modifier: Modifier = Modifier, name: String, salePrice: String, initialledPrice: String?) {
    Box(modifier = modifier
        .width(160.dp)
        .height(280.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(GrayMaterial.copy(alpha = 0.2f))
        .padding(bottom = 8.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Image",
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.favicon),
            contentDescription = "Image",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp, top = 16.dp)
            ,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = name,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = BlackMaterial,
                )
            )
            Row (

                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Text(
                    text = "$$salePrice",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackMaterial,
                    )
                )
                Text(
                    text = "$$initialledPrice",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                )
            }

        }
    }
}

@Composable
fun ProductRow(modifier: Modifier = Modifier){
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(20)
        { item ->
            ProductItem(name="Pant", salePrice="123", initialledPrice = null)
        }
    }
}

@Composable
fun DrawProductScreenContent(modifier: Modifier = Modifier,
                  paddingValues: PaddingValues,
                  navController: NavController
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp, vertical = paddingValues.calculateTopPadding()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
            item {
                SearchBarRow(navController = navController)
                Spacer(modifier = Modifier.height(4.dp))
            }
            item {
                TitleTextRow(content="Categories")
            }
            item {
                Category()
            }
            item{
                TitleTextRow(content="Top Selling")
            }
            item {
                ProductRow()
            }
            item {
                TitleTextRow(content="New In", color= MagentaMaterial)
            }
            item {
                ProductRow()
            }

        }
    }
}

@Composable
fun ProductScreen(modifier: Modifier = Modifier, navController: NavController, paddingValues: PaddingValues  = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Row(
                modifier=Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {}) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "Back",
                        modifier = Modifier.clip(CircleShape).size(48.dp)
                    )
                }

                Button(
                    onClick = {},
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(MagentaMaterial)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Men", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                        )
                    }
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier.clip(CircleShape).background(MagentaMaterial)
                        ,
                ) {
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = WhiteMaterial
                    )
                }
            }
        }
    ) {
        DrawProductScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController
        )
    }
}
@Preview(name="Summary", showBackground = true, showSystemUi = true)
@Composable
fun Summary()
{
    LadosTheme {
        ProductScreen(navController = NavController(LocalContext.current))
    }
}

