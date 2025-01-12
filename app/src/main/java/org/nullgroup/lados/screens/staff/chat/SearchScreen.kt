package org.nullgroup.lados.screens.staff.chat

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.compose.staff.chat.SearchRecentGrid
import org.nullgroup.lados.compose.staff.chat.SearchResultList
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.staff.SearchChatState
import org.nullgroup.lados.viewmodels.staff.SearchChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    searchChatViewModel: SearchChatViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val recentSearches = searchChatViewModel.recentSearches.collectAsState()
    val searchChatState = searchChatViewModel.searchChatState.collectAsState()
    var searchValue by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current
    val focusRequester by remember {
        mutableStateOf(FocusRequester())
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Scaffold(
        containerColor = LadosTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LadosTheme.colorScheme.background
                ),
                title = {
                    CustomTextField(
                        modifier = modifier
                            .focusRequester(focusRequester)
                            .height(48.dp),
                        textStyle = LadosTheme.typography.titleMedium.copy(
                            fontSize = 14.sp
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        text = searchValue,
                        onValueChange = {
                            searchValue = it
                            searchChatViewModel.onSearching(it)
                        },
                        label = "Search",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            modifier = Modifier.sizeIn(maxHeight = 28.dp),
                            painter = painterResource(id = R.drawable.arrowleft2),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }) {
                        Icon(
                            modifier = Modifier.sizeIn(maxHeight = 28.dp),
                            painter = painterResource(id = R.drawable.searchnormal1),
                            contentDescription = "Search"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalDivider(
            modifier = Modifier.padding(top = innerPadding.calculateTopPadding() + 4.dp),
            thickness = 1.dp
        )

        if (searchValue.isNotEmpty()) {
            Log.d("SearchScreen", "Search value: $searchValue")
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                when (searchChatState.value) {
                    is SearchChatState.Loading -> {
                        LoadOnProgress(
                            modifier = Modifier,
                            content = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator()
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Loading")
                                }
                            }
                        )
                    }

                    is SearchChatState.Error -> {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(text = (searchChatState.value as SearchChatState.Error).message)
                        }
                    }

                    is SearchChatState.Success -> {
                        SearchResultList(
                            modifier = Modifier,
                            listResult = (searchChatState.value as SearchChatState.Success).data
                        ) { user ->
                            searchChatViewModel.saveRecentSearch(user.id)
                            searchValue = user.name
                            searchChatViewModel.getRoomChatByUserId(user.id) {
                                navController?.navigate("${Screen.Staff.ChatWithCustomerScreen.route}/${it}")
                            }
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = "Recent Searches",
                    style = LadosTheme.typography.titleMedium.copy(
                        color = LadosTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (recentSearches.value.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No recent searches")
                    }
                } else {
                    SearchRecentGrid(recentlySearches = recentSearches.value.reversed()) { user ->
                        searchChatViewModel.getRoomChatByUserId(user.id) {
                            searchChatViewModel.saveRecentSearch(user.id)
                            navController?.navigate("${Screen.Staff.ChatWithCustomerScreen.route}/${it}")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    LadosTheme {
        SearchScreen()
    }
}
