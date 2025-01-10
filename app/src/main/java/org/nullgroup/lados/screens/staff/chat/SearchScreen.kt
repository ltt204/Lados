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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.compose.staff.chat.SearchRecentGrid
import org.nullgroup.lados.compose.staff.chat.SearchResultList
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.SearchChatState
import org.nullgroup.lados.viewmodels.SearchChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    searchChatViewModel: SearchChatViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
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
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        text = searchValue,
                        onValueChange = {
                            searchValue = it
                            searchChatViewModel.onSearching(it)
                        },
                        label = "Search",
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions =  KeyboardActions(
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

        /**TODO: There are three situations
         *  1. Search value is empty -> Show recent searches
         *  2. Search value is not empty -> Show search result
         *  3. User click on search button with not completed search value or click on a suggestions
         *      -> Navigate to search result screen
         *  Need to re-handle the conditions for better handling this three situations
         */
        if (searchValue.isNotEmpty()) {
            Log.d("SearchScreen", "Search value: $searchValue")
            // Show suggestions
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
                            searchValue = user.name
                            searchChatViewModel.getRoomChatByUserId(user.id) {
                                navController?.navigate("${Screen.Staff.ChatWithCustomerScreen.route}/${it}")
                            }
                        }
                    }
                }
            }
        } else {
            Log.d("SearchScreen", "Search value is empty")
            // Show recent searches
            Column(
                modifier = Modifier
                    .padding(top = innerPadding.calculateTopPadding())
                    .fillMaxSize()
            ) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = "Recent Searches",
                    style = LadosTheme.typography.titleMedium.copy(
                        color = LadosTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                SearchRecentGrid(recentlySearches = emptyList()) { user ->
                    //TODO: Get chat room id from searchScreenViewModel and navigate to chat screen
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
