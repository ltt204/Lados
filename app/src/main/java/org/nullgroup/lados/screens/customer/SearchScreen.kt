package org.nullgroup.lados.screens.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.SharedViewModel

@Composable
fun NormalTextFieldSearchScreen(
    label: String,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit),
) {
    val (text, setText) = mutableStateOf("")
    TextField(
        leadingIcon = icon,
        value = text,

        colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent),
        onValueChange = setText,
        // note: modify color
        label = { Text(text = label, color = LadosTheme.colorScheme.error, fontSize = 18.sp) },
        modifier = modifier
    )
}

@Composable
fun SearchBarSearchScreen(modifier: Modifier = Modifier) {
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
                // note: modify
                unfocusedBorderColor = LadosTheme.colorScheme.surfaceContainer,
                // note: modify
                focusedBorderColor = LadosTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun SearchHeaderSearchScreen(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleTextRow(contentLeft = "Search", contentRight = "Clear")
    }
}

@Composable
fun SearchHistoryRow(modifier: Modifier = Modifier, content: String) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = content,
            // note: modify color
            style = TextStyle(
                fontSize = 18.sp,
                color = LadosTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        )
        Spacer(Modifier.weight(1f))

        Box(
            modifier
                .clip(CircleShape)
                // note: modify color
                .border(BorderStroke(1.dp, LadosTheme.colorScheme.outline), shape = CircleShape)
                .padding(4.dp)
        ) {
            Icon(
                Icons.Outlined.Close,
                // note: modify
                tint = LadosTheme.colorScheme.outline,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SearchHistory(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
    ) {

        items(20) { item ->
            SearchHistoryRow(modifier, "History $item")
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun DrawMainSearchScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
            .padding(top = paddingValues.calculateTopPadding()),
    ) {
        SearchHeaderSearchScreen()
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            // note: modify
            color = LadosTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
        SearchHistory()
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    sharedViewModel: SharedViewModel = SharedViewModel(),
) {
    Scaffold(
        modifier = modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp),
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
                        // note: modify
                        .background(LadosTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Search",
                        // note: modify
                        tint = LadosTheme.colorScheme.outline

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
        DrawMainSearchScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
        )
    }
}
