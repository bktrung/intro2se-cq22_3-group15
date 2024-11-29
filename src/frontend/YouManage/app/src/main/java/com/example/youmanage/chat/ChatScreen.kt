package com.example.youmanage.chat

import android.Manifest
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youmanage.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.util.UUID

// Model dữ liệu tin nhắn
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val senderId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByUser: Boolean,
    val isAudioMessage: Boolean = false,
    val username: String = "User",
    val avatarUrl: String = ""

)

// ViewModel quản lý danh sách tin nhắn
//class ChatViewModel : ViewModel() {
//    private val _messages = MutableStateFlow<List<Message>>(emptyList())
//    val messages: StateFlow<List<Message>> = _messages
//    fun sendMessage(content: String, isAudio: Boolean = false) {
//        val newMessage = Message(
//            content = content,
//            senderId = "currentUser",
//            isSentByUser = true,
//            isAudioMessage = isAudio,
//            username = "You",
//            avatarUrl = ""
//        )
//        _messages.value = _messages.value + newMessage
//    }
//    fun receiveMessage(content: String, isAudio: Boolean = false) {
//        val newMessage = Message(
//            content = content,
//            senderId = "otherUser",
//            isSentByUser = false,
//            isAudioMessage = isAudio,
//            username = "Other User",
//            avatarUrl = ""
//        )
//        _messages.value = _messages.value + newMessage
//    }
//}

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    init {
        loadSampleMessages()
    }

    private fun loadSampleMessages() {
        val sampleMessages = listOf(
            Message(
                content = "Hello, how are you?",
                senderId = "currentUser",
                isSentByUser = true,
                isAudioMessage = false,
                username = "You",
                avatarUrl = ""
            ),
            Message(
                content = "I'm good, thank you! How about you?",
                senderId = "otherUser",
                isSentByUser = false,
                isAudioMessage = false,
                username = "Other User",
                avatarUrl = ""
            ),
            Message(
                content = "Here's an audio message!",
                senderId = "currentUser",
                isSentByUser = true,
                isAudioMessage = true,
                username = "You",
                avatarUrl = ""
            ),
            Message(
                content = "Got it, listening now.",
                senderId = "otherUser",
                isSentByUser = false,
                isAudioMessage = true,
                username = "Other User",
                avatarUrl = ""
            )
        )
        _messages.value = sampleMessages
    }

    fun sendMessage(content: String, isAudio: Boolean = false) {
        val newMessage = Message(
            content = content,
            senderId = "currentUser",
            isSentByUser = true,
            isAudioMessage = isAudio,
            username = "You",
            avatarUrl = ""
        )
        _messages.value = _messages.value + newMessage
    }

    fun receiveMessage(content: String, isAudio: Boolean = false) {
        val newMessage = Message(
            content = content,
            senderId = "otherUser",
            isSentByUser = false,
            isAudioMessage = isAudio,
            username = "Other User",
            avatarUrl = ""
        )
        _messages.value = _messages.value + newMessage
    }
}


@Composable
fun ChatScreen(
    messages: List<Message>,
    onMessageSent: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    AppColors.LightBlue,
                    AppColors.LighterBlue
                )
            )
        )

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header Box with title and back button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { /* Handle back action */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Project Chat",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages) { message ->
                    Column {
                        MessageBubble(
                            message = message,
                            isSentByUser = message.isSentByUser,
                            username = message.username,  // Assuming `username` is part of your message object
                            avatarUrl = message.avatarUrl // Assuming `avatarUrl` is part of your message object
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            // Input bar
            ChatInputBar(
                onMessageSent = onMessageSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message, isSentByUser: Boolean, username: String, avatarUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isSentByUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isSentByUser) Arrangement.End else Arrangement.Start
        ) {
            // Avatar for received messages
            if (!isSentByUser) {
                Avatar(icon = Icons.Default.Person, contentDescription = "Your Avatar") // Replace with appropriate icon or image
            }

            // Message content
            Column(
                horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
            ) {
                // Username for received messages
                if (!isSentByUser) {
                    Text(
                        text = username,
                        style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }

                // Message content (Audio or Text)
                if (message.isAudioMessage) {
                    AudioMessageBubble(
                        audioPath = message.content,
                        isSentByUser = isSentByUser
                    )
                } else {
                    TextMessageBubble(
                        content = message.content,
                        isSentByUser = isSentByUser
                    )
                }
            }

            // Avatar for sent messages (You can also add an icon or image here)
            if (isSentByUser) {
                Avatar(icon = Icons.Default.Person, contentDescription = "Your Avatar") // Replace with appropriate icon or image
            }
        }
    }
}

@Composable
fun Avatar(icon: ImageVector, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(40.dp) // Size of the avatar
            .clip(CircleShape) // Makes the avatar circular
            .background(AppColors.BackgroundColor) // Background color inside the circle
            .border(2.dp, AppColors.BackgroundColor, CircleShape) // Border color and shape
            .padding(8.dp) // Padding around the icon
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
        )
    }
}


@Composable
fun TextMessageBubble(content: String, isSentByUser: Boolean) {
    Text(
        text = content,
        modifier = Modifier
            .background(
                color = if (isSentByUser) AppColors.SentMessageColor else AppColors.ReceivedMessageColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isSentByUser) 16.dp else 0.dp,
                    bottomEnd = if (isSentByUser) 0.dp else 16.dp
                )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = AppColors.TextColor,
        style = TextStyle(
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily.SansSerif
        )
    )
}


@Composable
fun AudioMessageBubble(audioPath: String, isSentByUser: Boolean) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }

    // Initialize media player and load audio file
    LaunchedEffect(audioPath) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(audioPath)
            mediaPlayer.prepare()
            duration = mediaPlayer.duration
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Cleanup MediaPlayer resources
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
    // Update progress while playing
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (mediaPlayer.isPlaying) {
                progress = mediaPlayer.currentPosition / duration.toFloat()
                delay(100)
            }
        }
    }
    // Audio message UI
    Row(
        modifier = Modifier
            .background(
                color = if (isSentByUser) Color(0xFF007AFF) else Color(0xFFF1F0F0),
                shape = MaterialTheme.shapes.medium
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    isPlaying = false
                } else {
                    mediaPlayer.start()
                    isPlaying = true
                }
            },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause Audio" else "Play Audio"
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                color = Color.Gray
            )

            Text(
                text = "${(progress * duration / 1000).toInt()}s / ${duration / 1000}s",
                style = TextStyle(fontSize = 12.sp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

// Phát âm thanh từ file
fun playAudio(filePath: String) {
    val mediaPlayer = MediaPlayer().apply {
        setDataSource(filePath)
        prepare()
        start()
    }
}

@Composable
fun ChatInputBar(
    onMessageSent: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Thay đổi cách khởi tạo MediaRecorder để đảm bảo nó được tái khởi tạo khi cần
    var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    val outputFile = remember { File(context.getExternalFilesDir(null), "audio_message.3gp") }

    val microphonePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                // Thông báo nếu không cấp quyền
            }
        }
    )

    LaunchedEffect(Unit) {
        microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    // Thêm vào chức năng bắt đầu và dừng ghi âm
    fun startRecording() {
        // Khởi tạo lại MediaRecorder mỗi lần ghi âm bắt đầu
        mediaRecorder = MediaRecorder().apply {
            try {
                reset()  // Đặt lại trạng thái nếu có bất kỳ ghi âm nào trước đó
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopRecordingAndSend() {
        try {
            mediaRecorder?.apply {
                stop()
                release() // Giải phóng tài nguyên của MediaRecorder
            }
            // Gửi tin nhắn âm thanh
            onMessageSent(outputFile.absolutePath, true)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null // Đảm bảo giải phóng tài nguyên sau khi ghi âm
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (isRecording) {
                    // Dừng ghi âm và gửi tin nhắn
                    stopRecordingAndSend()
                } else {
                    // Bắt đầu ghi âm
                    startRecording()
                }
                isRecording = !isRecording
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_mic),
                contentDescription = "Mic Icon",
                modifier = Modifier.size(32.dp)
            )
        }

        // Phần còn lại của `ChatInputBar` để gửi tin nhắn văn bản
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
        }

        IconButton(onClick = {
            if (messageText.isNotBlank()) {
                onMessageSent(messageText, false)  // Gửi tin nhắn văn bản
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFB2EBF2), // Light blue
                    Color(0xFF80DEEA)  // Deeper blue
                )
            ))
    ) {
        ChatScreen(
            messages = messages,
            onMessageSent = { content, isAudio ->
                viewModel.sendMessage(content, isAudio)
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

object AppColors {
    val LightBlue = Color(0xFFCCF1F5)  // Lighter light blue
    val LighterBlue = Color(0xFFBEEFF4)
    val SentMessageColor = Color(0xFFDCF8C6)  // Sent by user
    val ReceivedMessageColor = Color(0xFFE5E7EB)  // Received by other user
    val PlayButtonColor = Color(0xFF007AFF)  // Color for play button
    val BackgroundColor = Color(0xFFF1F0F0)  // Background color for audio message
    val TextColor = Color(0xFF202124)  // Text color for messages
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChatScreenWithViewModel()
}
