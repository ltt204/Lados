package org.nullgroup.lados.screens.customer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.textclassifier.TextLinks.TextLink
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.BrownMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.WhiteMaterial
import org.nullgroup.lados.viewmodels.SharedViewModel

@Composable
fun NormalTextFieldSearchScreen(
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
        label = { Text(text = label, color = GrayMaterial, fontSize = 18.sp)},
        modifier = modifier

    )
}


@Composable
fun SearchBarSearchScreen(modifier: Modifier=Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50)),
            singleLine = true,
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search"
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
fun SearchHeaderSearchScreen(modifier: Modifier=Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
        ,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleTextRow(contentLeft = "Search", contentRight = "Clear")
    }
}

@Composable
fun SearchHistoryRow(modifier: Modifier=Modifier, content: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = content,
            style = TextStyle(fontSize = 18.sp, color = BlackMaterial.copy(alpha = 0.5f))
        )
        Spacer(Modifier.weight(1f))

        Box(
            modifier
                .clip(CircleShape)
                .border(BorderStroke(1.dp, BrownMaterial), shape = CircleShape)
                .padding(4.dp)
        ) {
            Icon(
                Icons.Outlined.Close,
                tint = BlackMaterial,
                contentDescription = null,
                modifier=Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SearchHistory(modifier: Modifier=Modifier){
    LazyColumn (
        modifier=modifier
            .fillMaxWidth()
        ,
    ){

        items(20){
                item ->
            SearchHistoryRow(modifier, "History $item")
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun DrawMainSearchScreenContent(
    modifier: Modifier=Modifier,
    navController: NavController,
    paddingValues: PaddingValues= PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(
                horizontal = 8.dp,
                vertical = paddingValues.calculateTopPadding()
            ),
    ) {
        SearchHeaderSearchScreen()
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(modifier = Modifier
            .fillMaxWidth(),
            color=GrayMaterial.copy(alpha=0.3f)
        )
        SearchHistory()
    }
}

@Composable
fun SearchScreen(modifier: Modifier = Modifier, navController: NavController, paddingValues: PaddingValues  = PaddingValues(horizontal = 16.dp, vertical = 8.dp), sharedViewModel: SharedViewModel = SharedViewModel()) {
    Scaffold(
        modifier = modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
        ,
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
                Spacer(modifier = Modifier.width(16.dp))
                SearchBar(modifier=Modifier.fillMaxWidth(), navController=navController, onSearch = {})
            }
        }
    ) {
        DrawMainSearchScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
        )
    }
}
