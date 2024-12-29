package org.nullgroup.lados.screens.customer.chat

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import org.nullgroup.lados.compose.signin.CustomTextField
import org.nullgroup.lados.data.models.Message
import org.nullgroup.lados.data.models.MessageType
import org.nullgroup.lados.screens.Screen
import org.nullgroup.lados.ui.theme.LadosTheme
import org.nullgroup.lados.viewmodels.customer.chat.ChatViewModel
import org.nullgroup.lados.viewmodels.customer.chat.events.ChatScreenEvent
import org.nullgroup.lados.viewmodels.customer.chat.states.ChatUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    paddingValues: PaddingValues = PaddingValues(0.dp),
) {
    val viewModel = hiltViewModel<ChatViewModel>()
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var messageText by remember {
        mutableStateOf("")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.handleEvent(ChatScreenEvent.SendImage(it))
        }
    }

    Box(
        modifier = modifier
            .padding(bottom = paddingValues.calculateBottomPadding())
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LadosTheme.size.medium)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = true,
            ) {
                items(messages.reversed()) { message ->
                    MessageItem(
                        message = message,
                        isFromCurrentUser = message.senderId == viewModel.getCurrentUserId(),
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
                    onClick = { launcher.launch("image/*") },
                    enabled = uiState !is ChatUiState.Loading
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

@Composable
fun MessageItem(
    message: Message,
    isFromCurrentUser: Boolean,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = LadosTheme.size.small / 2),
        horizontalAlignment = if (isFromCurrentUser) Alignment.End else Alignment.Start,
    ) {
        when (message.type) {
            MessageType.TEXT -> {
                Surface(
                    color = if (isFromCurrentUser)
                        LadosTheme.colorScheme.primaryContainer
                    else LadosTheme.colorScheme.surfaceContainer,
                    shape = LadosTheme.shape.medium,
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

        Text(
            text = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(message.timestamp)),
            style = LadosTheme.typography.bodySmall,
            color = LadosTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 2.dp
            ),
        )
    }
}