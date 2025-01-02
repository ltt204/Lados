package org.nullgroup.lados.screens.staff

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.AutoCompleteSearchBar
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.common.TwoColsItem
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.common.LoadingScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.getMessageHistoryTimeDisplayment
import org.nullgroup.lados.viewmodels.customer.chat.ChatViewModel
import org.nullgroup.lados.viewmodels.staff.StaffChatScreenUiState
import org.nullgroup.lados.viewmodels.staff.StaffChatViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    chatViewModel: ChatViewModel = hiltViewModel(),
    staffChatViewModel: StaffChatViewModel = hiltViewModel(),
) {
    val chatScreenUiState by staffChatViewModel.uiState.collectAsStateWithLifecycle()
    var userNames by remember {
        mutableStateOf(emptyList<String>())
    }
    Scaffold(
        modifier = modifier
            .padding(top = paddingValues.calculateTopPadding())
            .padding(horizontal = 16.dp),
        containerColor = LadosTheme.colorScheme.background,
        topBar = {
            //Search bar
            AutoCompleteSearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                label = "Search by user name",
                options = userNames,
                onOptionSelected = { value, index ->

                }
            )
        }
    ) { innerPadding ->
        when (chatScreenUiState) {
            is StaffChatScreenUiState.Success -> {
                val screenUiState = chatScreenUiState as StaffChatScreenUiState.Success
                userNames = screenUiState.data.keys.map { it.name }
                LazyColumn(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                ) {
                    items(screenUiState.data.keys.toList(), { it.id }) { user ->
                        val chatRoom = screenUiState.data[user]!!
                        Log.d("ChatScreen", "ChatRoom: $chatRoom")
                        ChatRoomItem(
                            chatRoom = chatRoom,
                            user = user,
                            onChatRoomItemClick = {
                                Log.d("ChatScreen", "ChatRoom: \"${Screen.Staff.ChatWithCustomerScreen.route}/${chatRoom.id}\"")
                                navController?.navigate("${Screen.Staff.ChatWithCustomerScreen.route}/${chatRoom.id}")
                            }
                        )
                    }
                }
            }

            is StaffChatScreenUiState.Loading -> {
                //Loading
                LoadingScreen()
            }

            is StaffChatScreenUiState.Error -> {
                //Error
            }
        }
    }
}

@Composable
fun ChatRoomItem(
    chatRoom: ChatRoom,
    user: User,
    onChatRoomItemClick: (ChatRoom) -> Unit = {},
) {
    TwoColsItem(
        colors = CardDefaults.cardColors(
            containerColor = LadosTheme.colorScheme.background,
            contentColor = LadosTheme.colorScheme.onPrimaryContainer,
            disabledContainerColor = LadosTheme.colorScheme.outline,
        ),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.Center,
                    loading = {
                        LoadOnProgress(
                            modifier = Modifier
                                .clip(CircleShape)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.padding(top = 16.dp))
                        }
                    },
                    model = ImageRequest
                        .Builder(context = LocalContext.current)
                        .data(user.avatarUri)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Profile Picture"
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Text(
                        text = user.name, style = LadosTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        )
                    )
                    Text(
                        modifier = Modifier.widthIn(max = 172.dp),
                        text = chatRoom.lastMessage,
                        style = LadosTheme.typography.bodySmall,
                        maxLines = 1,
                        softWrap = true,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = chatRoom.lastMessageTime.getMessageHistoryTimeDisplayment(),
                    style = LadosTheme.typography.bodySmall
                )
            }
        },
        onClick = {
            onChatRoomItemClick(chatRoom)
        }
    )
}

@Preview
@Composable
fun ChatRoomItemPreview() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    LadosTheme {
        Surface {
            Column {
                ChatRoomItem(
                    chatRoom = ChatRoom(
                        id = "1",
                        customerId = "1",
                        lastMessage = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        lastMessageTime = System.currentTimeMillis(),
                    ),
                    user = User(
                        name = "John Doe",
                        avatarUri = "https://example.com/avatar.jpg"
                    ),
                )

                ChatRoomItem(
                    chatRoom = ChatRoom(
                        id = "1",
                        customerId = "1",
                        lastMessage = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        lastMessageTime = dateFormat.parse("2024-12-20 00:00:00")!!.time,
                    ),
                    user = User(
                        name = "John Doe",
                        avatarUri = "https://example.com/avatar.jpg"
                    ),
                )

                ChatRoomItem(
                    chatRoom = ChatRoom(
                        id = "1",
                        customerId = "1",
                        lastMessage = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                        lastMessageTime = dateFormat.parse("2025-01-01 00:00:00")!!.time,
                    ),
                    user = User(
                        name = "John Doe",
                        avatarUri = "https://example.com/avatar.jpg"
                    ),
                )
            }
        }
    }
}