package com.example.youmanage.chat

import android.Manifest
import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
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
    val content: String,  // Nội dung tin nhắn (hoặc văn bản, hoặc đường dẫn file âm thanh)
    val senderId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSentByUser: Boolean,
    val isAudioMessage: Boolean = false  // Cờ để xác định đây là tin nhắn âm thanh hay không
)

// ViewModel quản lý danh sách tin nhắn
class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    // Gửi tin nhắn
    fun sendMessage(content: String, isAudio: Boolean = false) {
        val newMessage = Message(content = content, senderId = "currentUser", isSentByUser = true, isAudioMessage = isAudio)
        _messages.value = _messages.value + newMessage  // Thêm tin nhắn vào danh sách
    }

    // Nhận tin nhắn từ người khác (giả lập)
    fun receiveMessage(content: String, isAudio: Boolean = false) {
        val newMessage = Message(content = content, senderId = "otherUser", isSentByUser = false, isAudioMessage = isAudio)
        _messages.value = _messages.value + newMessage  // Thêm tin nhắn vào danh sách
    }
}

// Giao diện chính của màn hình chat
@Composable
fun ChatScreen(
    messages: List<Message>,
    onMessageSent: (String, Boolean) -> Unit  // Cập nhật đối số để truyền loại tin nhắn (văn bản hay âm thanh)
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
        if (message.isAudioMessage) {
            // Hiển thị nút "Phát" cho tin nhắn âm thanh
            IconButton(onClick = { playAudio(message.content) }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play Audio")
            }
        } else {
            // Hiển thị tin nhắn văn bản
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

    ChatScreen(
        messages = messages,
        onMessageSent = { content, isAudio ->
            viewModel.sendMessage(content, isAudio)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChatScreenWithViewModel()
}
