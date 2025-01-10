package org.nullgroup.lados.screens.staff.chat

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.screens.customer.chat.MessageItem
import org.nullgroup.lados.screens.customer.profile.LoadingContent
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.chat.ChatViewModel
import org.nullgroup.lados.viewmodels.customer.chat.events.ChatScreenEvent
import org.nullgroup.lados.viewmodels.customer.chat.states.ChatUiState
import org.nullgroup.lados.viewmodels.staff.MessageUiState
import org.nullgroup.lados.viewmodels.staff.StaffChatWithCustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatWithCustomerScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    paddingValues: PaddingValues = PaddingValues(0.dp),
    chatViewModel: ChatViewModel = hiltViewModel(),
    staffChatViewModel: StaffChatWithCustomerViewModel = hiltViewModel()
) {
    val uiState by staffChatViewModel.msgUiState.collectAsState()
    var messageText by remember {
        mutableStateOf("")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            chatViewModel.handleEvent(ChatScreenEvent.SendImage(it))
        }
    }
    when (uiState) {
        is MessageUiState.Error -> {
            (uiState as ChatUiState.Error).message?.let {
                Text(
                    text = it,
                    style = LadosTheme.typography.bodyLarge,
                    color = LadosTheme.colorScheme.error,
                )
            }
        }

        is MessageUiState.Loading -> {
            LoadingContent()
        }

        is MessageUiState.Success -> {
            Scaffold(
                modifier = modifier
                    .padding(
                        bottom = paddingValues.calculateBottomPadding()
                    )
                    .fillMaxSize(),
                containerColor = LadosTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = LadosTheme.colorScheme.background,
                        ),
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SubcomposeAsyncImage(
                                    modifier = Modifier
                                        .size(46.dp)
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
                                        .data(staffChatViewModel.chatWith.second)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Profile Picture"
                                )
                                Text(
                                    text = staffChatViewModel.chatWith.first,
                                    style = LadosTheme.typography.titleLarge.copy(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = LadosTheme.colorScheme.onBackground,
                                )
                            }
                        }, navigationIcon = {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.arrowleft2),
                                    contentDescription = null
                                )
                            }
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = innerPadding.calculateTopPadding())
                        .fillMaxSize()
                ) {

                    val messages = (uiState as MessageUiState.Success).messages
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reverseLayout = true,
                    ) {
                        items(items = messages.reversed(), key = { it.id }) { message ->
                            Log.d("ChatWithCustomerScreen", "Sender: ${message.senderId} \n current: ${staffChatViewModel.currentStaff.id}")
                            MessageItem(
                                message = message,
                                isFromCurrentUser = message.senderId == staffChatViewModel.currentStaff.id,
                                onProductClick = { productId ->
                                    navController.navigate("${Screen.Customer.ProductDetailScreen.route}/$productId")
                                }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                launcher.launch("image/*")
                            },
                            enabled = uiState !is MessageUiState.Loading
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Send image",
                                tint = LadosTheme.colorScheme.primary,
                            )
                        }

                        CustomTextField(
                            text = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            label = "Type a message...",
                        )

                        IconButton(
                            onClick = {
                                if (messageText.isNotEmpty()) {
                                    chatViewModel.handleEvent(ChatScreenEvent.SendText(messageText))
                                    messageText = ""
                                }
                            },
                            enabled = uiState !is MessageUiState.Loading && messageText.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send message",
                                tint = LadosTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }

        }
    }
}