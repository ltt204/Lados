package org.nullgroup.lados.screens.staff.chat

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.ChatRoom
import org.nullgroup.lados.data.models.User
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.common.LoadingScreen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.getMessageHistoryTimeDisplayment
import org.nullgroup.lados.viewmodels.staff.DeleteChatRoomUiState
import org.nullgroup.lados.viewmodels.staff.StaffChatScreenUiState
import org.nullgroup.lados.viewmodels.staff.StaffChatViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    staffChatViewModel: StaffChatViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope()
) {
    val chatScreenUiState by staffChatViewModel.uiState.collectAsStateWithLifecycle()
    val deleteChatRoomUiState by staffChatViewModel.deleteChatRoomUiState.collectAsStateWithLifecycle()

    var selectedChatroom by remember {
        mutableStateOf(null as String?)
    }
    var onDeleting by remember {
        mutableStateOf(false)
    }
    val sheetState = rememberModalBottomSheetState()

    var userNames by remember {
        mutableStateOf(emptyList<String>())
    }
    Scaffold(
        modifier = modifier
            .padding(top = paddingValues.calculateTopPadding())
            .padding(horizontal = 16.dp),
        containerColor = LadosTheme.colorScheme.background,
        topBar = {
            Box(modifier = Modifier
                .padding(bottom = 16.dp)
                .clickable {
                    navController?.navigate(Screen.Staff.SearchScreen.route)
                }
            ) {
                CustomTextField(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(24.dp),
                    text = "",
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.searchnormal1),
                            contentDescription = "Dropdown"
                        )
                    },
                    onValueChange = {
                    },
                    label = "Search",
                    singleLine = true,
                )
            }
        }
    ) { innerPadding ->
        when (chatScreenUiState) {
            is StaffChatScreenUiState.Success -> {
                val screenUiState = chatScreenUiState as StaffChatScreenUiState.Success
                userNames = screenUiState.data.keys.map { it.name }
                LazyColumn(
                    modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(screenUiState.data.keys.toList(), { it.id }) { user ->
                        val chatRoom = screenUiState.data[user]!!
                        Log.d("ChatScreen", "ChatRoom: $chatRoom")
                        Log.d("ChatScreen", "Current User: ${staffChatViewModel.currentUser.id}")
                        Log.d("ChatScreen", "Last message sent by: ${chatRoom.lastMessageSendBy}")
                        ChatRoomItem(
                            chatRoom = chatRoom,
                            user = user,
                            isCurrentUser = staffChatViewModel.currentUser.id == chatRoom.lastMessageSendBy,
                            onChatRoomItemClick = {
                                staffChatViewModel.markMessagesAsRead(chatRoom.id)
                                navController?.navigate("${Screen.Staff.ChatWithCustomerScreen.route}/${chatRoom.id}")
                            },
                            onLongClick = {
                                selectedChatroom = chatRoom.id
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

        if (selectedChatroom != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    selectedChatroom = null
                },
                sheetState = sheetState,
            ) {
                TextButton(
                    modifier = Modifier
                        .height(84.dp)
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                        if (!sheetState.isVisible) {
                            selectedChatroom = null
                        }
                        onDeleting = true
                        staffChatViewModel.removeChatRoom(selectedChatroom!!)
                    }) {
                    Text(
                        "Remove Chat",
                        style = LadosTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = LadosTheme.colorScheme.error
                        )
                    )
                }
            }
        }

        if (onDeleting) {
            when (deleteChatRoomUiState) {
                is DeleteChatRoomUiState.Loading -> {
                    LoadOnProgress(
                        modifier = Modifier,
                        content = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Deleting...")
                            }
                        }
                    )
                }

                is DeleteChatRoomUiState.Error -> {
                    Toast.makeText(
                        LocalContext.current,
                        (deleteChatRoomUiState as DeleteChatRoomUiState.Error).message,
                        Toast.LENGTH_SHORT
                    ).show()
                    onDeleting = false
                }

                is DeleteChatRoomUiState.Success -> {
                    Toast.makeText(LocalContext.current, "Chat Room Deleted", Toast.LENGTH_SHORT)
                        .show()
                    onDeleting = false
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatRoomItem(
    chatRoom: ChatRoom,
    user: User,
    isCurrentUser: Boolean = false,
    onChatRoomItemClick: (ChatRoom) -> Unit = {},
    onLongClick: () -> Unit = {},
) {
    val unreadChatRoom = chatRoom.unreadCount > 0
    Log.d("ChatRoomItem", "ChatRoom: $chatRoom")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp, max = 72.dp)
            .combinedClickable(
                onClick = {
                    onChatRoomItemClick(chatRoom)
                },
                onLongClick = {
                    onLongClick()
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = LadosTheme.colorScheme.background.copy(alpha = 0.6f),
            contentColor = LadosTheme.colorScheme.onBackground,
            disabledContainerColor = LadosTheme.colorScheme.outline,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight()
                .background(
                    color = LadosTheme.colorScheme.background,
                    shape = RoundedCornerShape(8.dp)
                ),
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
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = user.name, style = LadosTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    )
                )
                Log.d("ChatRoomItem", "ChatRoom: $chatRoom")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    modifier = Modifier.widthIn(max = 172.dp),
                    text = if (isCurrentUser) "You: ${chatRoom.lastMessage}" else chatRoom.lastMessage,
                    style = LadosTheme.typography.bodySmall.copy(
                        fontWeight = if (unreadChatRoom && !isCurrentUser) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    softWrap = true,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                if (unreadChatRoom && !isCurrentUser) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(LadosTheme.colorScheme.primary)
                    )
                }
                Text(
                    text = chatRoom.lastMessageTime.getMessageHistoryTimeDisplayment(),
                    style = LadosTheme.typography.bodySmall.copy(
                        fontWeight = if (unreadChatRoom && !isCurrentUser) FontWeight.SemiBold else FontWeight.Normal,
                    )
                )
            }
        }
    }
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
                        unreadCount = 2
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