package org.nullgroup.lados.screens.customer

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import org.nullgroup.lados.data.local.SearchHistoryManager
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.ui.theme.OnSurface
import org.nullgroup.lados.ui.theme.Tertiary
import org.nullgroup.lados.ui.theme.Outline
import org.nullgroup.lados.viewmodels.SharedViewModel

@Composable
fun SearchHeaderSearchScreen(
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "History",
            style = LadosTheme.typography.titleLarge.copy(
                color = LadosTheme.colorScheme.onBackground,
            )
        )
        TextButton(onClick = onClear) {
            Text(
                text = "Clear",
                style = LadosTheme.typography.titleLarge.copy(
                    color = LadosTheme.colorScheme.primary,
                )
            )
        }
    }
}

@Composable
fun SearchHistoryRow(
    modifier: Modifier = Modifier,
    content: String,
    onDelete: (String) -> Unit,
    onReClick: (String) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(modifier = Modifier
            .fillMaxWidth(0.9f)
            .clickable { onReClick(content) }) {
            Text(
                text = content,
                style = TextStyle(fontSize = 18.sp, color = OnSurface.copy(alpha = 0.5f))
            )
        }
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
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onDelete(content) }
            )
        }
    }
}

@Composable
fun SearchHistory(
    modifier: Modifier = Modifier,
    searchHistory: List<String>,
    onDelete: (String) -> Unit,
    onReClick: (String) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        items(searchHistory) { item ->
            SearchHistoryRow(
                modifier = modifier,
                content = item,
                onDelete = onDelete,
                onReClick = onReClick
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun DrawMainSearchScreenContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    searchHistory: List<String>,
    context: Context,
    searchHistoryManager: SearchHistoryManager,
    onDelete: (String) -> Unit,
    onReClick: (String) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 8.dp)
            .padding(top = paddingValues.calculateTopPadding())
    ) {
        SearchHeaderSearchScreen(onClear = {
            (context as ComponentActivity).lifecycleScope.launch {
                searchHistoryManager.clearSearchHistory()
            }
        })
        Spacer(Modifier.height(4.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = Outline.copy(alpha = 0.3f)
        )
        SearchHistory(
            searchHistory = searchHistory,
            onDelete = onDelete,
            onReClick = onReClick
        )
    }
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    paddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    sharedViewModel: SharedViewModel = SharedViewModel(),
    context: Context,
) {
    val searchHistoryManager = remember { SearchHistoryManager(context) }
    val searchHistory = searchHistoryManager.searchHistory.collectAsState(initial = emptySet())

    Scaffold(
        modifier = modifier.padding(paddingValues),
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
                        .background(LadosTheme.colorScheme.surfaceContainerHighest)
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Search",
                        tint = LadosTheme.colorScheme.onBackground,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    navController = navController,
                    onSearch = { query ->
                        (context as ComponentActivity).lifecycleScope.launch {
                            searchHistoryManager.addSearchQuery(query)
                        }
                        sharedViewModel.updateSearchQuery(query)
                        sharedViewModel.updateTypeScreen("In Search")
                        navController.navigate(
                            Screen.Customer.DisplayProductInCategory.route
                        )
                    }
                )
            }
        }
    ) {
        DrawMainSearchScreenContent(
            modifier = modifier,
            paddingValues = it,
            navController = navController,
            searchHistory = searchHistory.value.toList(),
            context = context,
            searchHistoryManager = searchHistoryManager,
            onDelete = { query ->
                // Delete the query
                (context as ComponentActivity).lifecycleScope.launch {
                    searchHistoryManager.deleteSearchQuery(query)
                }
            },
            onReClick = { query ->
                sharedViewModel.updateSearchQuery(query)
                sharedViewModel.updateTypeScreen("In Search")
                navController.navigate(
                    Screen.Customer.DisplayProductInCategory.route
                )
            }
        )
    }
}
