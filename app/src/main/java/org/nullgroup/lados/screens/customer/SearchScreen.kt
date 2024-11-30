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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.nullgroup.lados.ui.theme.BlackMaterial
import org.nullgroup.lados.ui.theme.BrownMaterial
import org.nullgroup.lados.ui.theme.GrayMaterial
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.WhiteMaterial

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
            .padding(horizontal = 8.dp)
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        NormalTextFieldSearchScreen(
            label = "Search", modifier = modifier
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                .background(Color.Transparent)
                .fillMaxHeight()
                .fillMaxWidth()
                .
                border(
                    BorderStroke(1.dp, GrayMaterial.copy(alpha=0.3f)),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                )
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = "Search",
                tint = BrownMaterial,
                modifier = Modifier.size(32.dp)

            )
        }
    }
}

@Composable
fun SearchHeaderSearchScreen(modifier: Modifier=Modifier) {
    Row(
        modifier = modifier
        ,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recent", style = TextStyle(
                fontSize = 24.sp,
                color = BrownMaterial
            )
        )
        Spacer(Modifier.weight(1f))
        LinkText("Clear all", "https://google.com", BrownMaterial, SpanStyle(color= BrownMaterial, fontSize = 18.sp))
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
fun MainSearchScreen(modifier: Modifier=Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
    ) {
        SearchBarSearchScreen()
        Spacer(Modifier.height(16.dp))

        SearchHeaderSearchScreen()
        Spacer(Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier
            .fillMaxWidth(),

            color=GrayMaterial.copy(alpha=0.3f)
        )
        Spacer(Modifier.height(16.dp))
        SearchHistory()
    }
}

@Preview(name="Search And Filter Review", showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview2() {
    LadosTheme {
        //SearchHeaderSearchScreen()
        //SearchHistory()
        MainSearchScreen()
    }
}