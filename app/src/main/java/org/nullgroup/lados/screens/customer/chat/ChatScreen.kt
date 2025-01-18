package org.nullgroup.lados.screens.customer.chat

import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.firebase.Timestamp
import org.nullgroup.lados.R
import org.nullgroup.lados.compose.common.LoadOnProgress
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.MessageType
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.utilities.formatToRelativeTime
import org.nullgroup.lados.utilities.getMessageTimeGapBetweenTwoMessagesDisplayment
import org.nullgroup.lados.utilities.toDateTimeString
import org.nullgroup.lados.viewmodels.customer.chat.ChatViewModel
import org.nullgroup.lados.viewmodels.customer.chat.events.ChatScreenEvent
import org.nullgroup.lados.viewmodels.customer.chat.states.ChatUiState
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val viewModel = hiltViewModel<ChatViewModel>()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.handleEvent(ChatScreenEvent.SendImage(it, context))
        }
    }

    Scaffold(
        modifier = modifier
            .padding(bottom = paddingValues.calculateBottomPadding()),
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
                        Image(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(id = R.drawable.lados_app_icon),
                            contentDescription = "App logo",
                            colorFilter = ColorFilter.tint(LadosTheme.colorScheme.onBackground),
                        )
                        Text(
                            text = "Lados",
                            style = LadosTheme.typography.titleLarge.copy(
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
                            contentDescription = null,
                            tint = LadosTheme.colorScheme.onBackground,
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(LadosTheme.size.medium)
            ) {
                val reversedMessages = messages.reversed()
                Log.d("ChatScreen", "Messages: ${reversedMessages.map { it.content }}")
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true,
                ) {
                    itemsIndexed(reversedMessages) { index, prevMessage ->
                        val nextMessage =
                            if (index == 0) prevMessage else reversedMessages[index - 1]
                        Log.d("ChatScreen", "Index: $index || Index-1: ${index - 1}")
                        Log.d(
                            "ChatScreen",
                            "Message: ${nextMessage.content} ${
                                nextMessage.timestamp.toDateTimeString(
                                    "dd-MM-yyyy hh:mm:ss"
                                )
                            }\nPrevious Message: ${prevMessage.content} ${
                                prevMessage.timestamp.toDateTimeString("dd-MM-yyyy hh:mm:ss")
                            }"
                        )
                        Log.d(
                            "ChatScreen",
                            "Time Gap: ${
                                nextMessage.timestamp.getMessageTimeGapBetweenTwoMessagesDisplayment(
                                    prevMessage.timestamp
                                )
                            }"
                        )
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = nextMessage.timestamp.getMessageTimeGapBetweenTwoMessagesDisplayment(
                                    prevMessage.timestamp
                                ),
                                modifier = Modifier.padding(top = 8.dp),
                                style = LadosTheme.typography.bodySmall,
                                color = LadosTheme.colorScheme.onBackground,
                            )
                        }
                        MessageItem(
                            message = prevMessage,
                            isFromCurrentUser = prevMessage.senderId == viewModel.getCurrentUserId(),
                            onProductClick = { productId ->
                                navController.navigate("${Screen.Customer.ProductDetailScreen.route}/$productId")
                            }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    LadosTheme.colorScheme.primary,
                                    LadosTheme.colorScheme.secondary
                                )
                            ),
                            shape = RoundedCornerShape(50)
                        ), contentAlignment = Alignment.Center
                    )
                    {
                        IconButton(
                            onClick = { launcher.launch("image/*") },
                            enabled = uiState !is ChatUiState.Loading,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Send image",
                                tint = LadosTheme.colorScheme.primary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    CustomTextField(
                        text = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(max = 52.dp),
                        label = "Type a message...",
                        shape = RoundedCornerShape(32.dp),
                        textStyle = LadosTheme.typography.bodyMedium,
                    )

                    IconButton(
                        onClick = {
                            if (messageText.isNotEmpty()) {
                                viewModel.handleEvent(ChatScreenEvent.SendText(messageText))
                                messageText = ""
                            }
                        },
                        enabled = uiState !is ChatUiState.Loading && messageText.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send message",
                            tint = LadosTheme.colorScheme.primary,
                        )
                    }
                }
            }

            if (uiState is ChatUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MessageItem(
    modifier: Modifier = Modifier,
    message: Message,
    userAvatar: String? = null,
    isFromCurrentUser: Boolean,
    onProductClick: (String) -> Unit,
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LadosTheme.size.small / 2),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!isFromCurrentUser) {
                Box(
                    modifier = Modifier.border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                LadosTheme.colorScheme.primary,
                                LadosTheme.colorScheme.secondary
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    ), contentAlignment = Alignment.Center
                ) {
                    if (userAvatar != null) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .size(28.dp)
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
                                .data(userAvatar)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture"
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(28.dp),
                            painter = painterResource(id = R.drawable.lados_app_icon),
                            colorFilter = ColorFilter.tint(LadosTheme.colorScheme.onBackground),
                            contentDescription = "Lados icon"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(4.dp))

            }
            when (message.type) {
                MessageType.TEXT -> {
                    Surface(
                        color = if (isFromCurrentUser)
                            LadosTheme.colorScheme.primaryContainer
                        else LadosTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(24.dp),
                        tonalElevation = 2.dp,
                    ) {
                        Text(
                            text = message.content,
                            modifier = Modifier.padding(LadosTheme.size.normal),
                            color = if (isFromCurrentUser)
                                LadosTheme.colorScheme.onPrimaryContainer
                            else LadosTheme.colorScheme.onSurface,
                        )
                    }
                }

                MessageType.IMAGE -> {
                    Surface(
                        shape = LadosTheme.shape.medium,
                        tonalElevation = 2.dp,
                    ) {
                        AsyncImage(
                            model = message.imageUrl,
                            contentDescription = message.content,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(LadosTheme.shape.medium),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                MessageType.PRODUCT -> {
                    Surface(
                        modifier = Modifier
                            .clickable {
                                message.productId?.let {
                                    onProductClick(it)
                                }
                            },
                        color = LadosTheme.colorScheme.surfaceContainer,
                        shape = LadosTheme.shape.medium,
                        tonalElevation = 2.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(LadosTheme.size.small)
                                .width(200.dp),
                            horizontalArrangement = Arrangement.spacedBy(LadosTheme.size.small)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Product",
                                tint = LadosTheme.colorScheme.primary,
                            )

                            Column {
                                Text(
                                    text = "Product Shared",
                                    style = LadosTheme.typography.titleSmall,
                                color = LadosTheme.colorScheme.onBackground,
                                )

                                Text(
                                    text = "Click to view details",
                                    style = LadosTheme.typography.bodySmall,
                                    color = LadosTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

        }

        Text(
            text = formatToRelativeTime(Timestamp(Date(message.timestamp))),
            style = LadosTheme.typography.bodySmall,
            color = LadosTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 2.dp
            ),
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun MessageItemPreview() {
    LadosTheme {
        MessageItem(
            message = Message(
                senderId = "1",
                content = "Hello",
                type = MessageType.TEXT,
            ),
            isFromCurrentUser = false,
            onProductClick = {},
        )
    }
}