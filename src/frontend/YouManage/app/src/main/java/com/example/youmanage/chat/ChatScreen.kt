package com.example.youmanage.chat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youmanage.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// Model dữ liệu tin nhắn
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val senderId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByUser: Boolean
)

// ViewModel quản lý danh sách tin nhắn
class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // Gửi tin nhắn
    fun sendMessage(content: String) {
        val newMessage = Message(content = content, senderId = "currentUser", isSentByUser = true)
        _messages.value = _messages.value + newMessage  // Thêm tin nhắn vào danh sách
    }

    // Nhận tin nhắn từ người khác (giả lập)
    fun receiveMessage(content: String) {
        val newMessage = Message(content = content, senderId = "otherUser", isSentByUser = false)
        _messages.value = _messages.value + newMessage  // Thêm tin nhắn vào danh sách
    }
}

// Giao diện chính của màn hình chat
@Composable
fun ChatScreen(
    messages: List<Message>,
    onMessageSent: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Project Chat",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primary
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(messages) { message ->
                MessageBubble(message = message, isSentByUser = message.isSentByUser)
            }
        }

        ChatInputBar(
            onMessageSent = onMessageSent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

// Giao diện bong bóng tin nhắn
@Composable
fun MessageBubble(message: Message, isSentByUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isSentByUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Text(
            text = message.content,
            modifier = Modifier
                .background(
                    if (isSentByUser) Color(0xFFF3A583) else Color(0xFF75FB6B),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(8.dp),
            color = Color.Black,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}

@Composable
fun ChatInputBar(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = MaterialTheme.shapes.medium)
                    .padding(start = 16.dp, end = 40.dp, top = 12.dp, bottom = 12.dp),
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
                decorationBox = { innerTextField ->
                    if (messageText.isEmpty()) {
                        Text("Aa", color = Color.Gray, fontSize = 18.sp)
                    }
                    innerTextField()
                }
            )

            IconButton(
                onClick = { /* Mở menu chọn biểu cảm */ },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_mood),
                    contentDescription = "Emoji"
                )
            }
        }

        IconButton(onClick = {
            if (messageText.isNotBlank()) {
                onMessageSent(messageText)
                messageText = ""
            }
        }) {
            Icon(Icons.Default.Send, contentDescription = "Send Message", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// Tích hợp ViewModel với ChatScreen để xử lý trạng thái
@Composable
fun ChatScreenWithViewModel(viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()

    ChatScreen(
        messages = messages,
        onMessageSent = { content ->
            viewModel.sendMessage(content)
        }
    )

    LaunchedEffect(Unit) {
        delay(3000)
        viewModel.receiveMessage("Hello from other user!")
    }
}

// Preview giao diện ChatScreen
@Composable
@Preview(showBackground = true)
fun PreviewChatScreen() {
    ChatScreen(
        messages = listOf(
            Message(content = "Hello", senderId = "currentUser", isSentByUser = true),
            Message(content = "Hi", senderId = "otherUser", isSentByUser = false)
        ),
        onMessageSent = {}
    )
}

