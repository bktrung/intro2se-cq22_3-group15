package com.example.youmanage.screens.chat

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.chat.Message
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.screens.task_management.TopBar
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ChatViewModel

@Preview(showBackground = true)
@Composable
fun ChatScreen(
    messages: List<Message> = emptyList(),
    onMessageSent: (String, Boolean) -> Unit = { _, _ -> },
    userId: Int = 1,
    onNavigateBack: () -> Unit = {},
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp),
        topBar = {
            com.example.youmanage.screens.project_management.TopBar(
                title = "Chat Room",
                color = Color.Transparent,
                onNavigateBack = onNavigateBack,
                trailing = {
                    Spacer(modifier = Modifier.size(24.dp))
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                onMessageSent = onMessageSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(vertical = 16.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.LightBlue,
                            AppColors.LighterBlue
                        )
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                val listState = rememberLazyListState()

                LaunchedEffect(messages.size) {
                    if (messages.isNotEmpty()) {
                        listState.animateScrollToItem(index = messages.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    items(messages.size) { index ->
                        Column {
                            MessageBubble(
                                message = messages[index],
                                isSentByUser = messages[index].author.id == userId,
                                username = messages[index].author.username,
                                avatarUrl = messages[index].author.username
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }

            }
        }
    }
}


@Composable
fun MessageBubble(
    message: Message = Message(User(), "Yes", 0, 0, ""),
    isSentByUser: Boolean = false,
    username: String = "Tuong",
    avatarUrl: String = ""
) {
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
                Avatar(icon = Icons.Default.Person, contentDescription = "Your Avatar")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Message content
            Column(
                horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
            ) {
                // Username for received messages
                if (!isSentByUser) {
                    Text(
                        text = username,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Gray),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }


                // Message content (Audio or Text)
//                if (message.isAudioMessage) {
//                    AudioMessageBubble(
//                        audioPath = message.content,
//                        isSentByUser = isSentByUser
//                    )
//                } else {
                TextMessageBubble(
                    content = message.content,
                    isSentByUser = isSentByUser
                )
                //  }
            }

            if(isSentByUser){
                Spacer(modifier = Modifier.width(8.dp))
            }


            // Avatar for sent messages (You can also add an icon or image here)
//            if (isSentByUser) {
//                Avatar(icon = Icons.Default.Person, contentDescription = "Your Avatar")
//            }
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
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = AppColors.TextColor,
        style = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp
        )
    )
}


//@Composable
//fun AudioMessageBubble(audioPath: String, isSentByUser: Boolean) {
//    val context = LocalContext.current
//    val mediaPlayer = remember { MediaPlayer() }
//    var isPlaying by remember { mutableStateOf(false) }
//    var duration by remember { mutableIntStateOf(0) }
//    var progress by remember { mutableFloatStateOf(0f) }
//
//    // Initialize media player and load audio file
//    LaunchedEffect(audioPath) {
//        try {
//            mediaPlayer.reset()
//            mediaPlayer.setDataSource(audioPath)
//            mediaPlayer.prepare()
//            duration = mediaPlayer.duration
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    // Cleanup MediaPlayer resources
//    DisposableEffect(Unit) {
//        onDispose {
//            mediaPlayer.release()
//        }
//    }
//    // Update progress while playing
//    LaunchedEffect(isPlaying) {
//        if (isPlaying) {
//            while (mediaPlayer.isPlaying) {
//                progress = mediaPlayer.currentPosition / duration.toFloat()
//                delay(100)
//            }
//        }
//    }
//    // Audio message UI
//    Row(
//        modifier = Modifier
//            .background(
//                color = if (isSentByUser) Color(0xFF007AFF) else Color(0xFFF1F0F0),
//                shape = MaterialTheme.shapes.medium
//            )
//            .padding(8.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        IconButton(
//            onClick = {
//                if (mediaPlayer.isPlaying) {
//                    mediaPlayer.pause()
//                    isPlaying = false
//                } else {
//                    mediaPlayer.start()
//                    isPlaying = true
//                }
//            },
//            modifier = Modifier.size(40.dp)
//        ) {
//            Icon(
//                imageVector = if (isPlaying) Icons.Default.PlayArrow else Icons.Default.PlayArrow,
//                contentDescription = if (isPlaying) "Pause Audio" else "Play Audio"
//            )
//        }
//
//        Column(modifier = Modifier.weight(1f)) {
//            LinearProgressIndicator(
//                progress = progress,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 8.dp),
//                color = Color.Gray
//            )
//
//            Text(
//                text = "${(progress * duration / 1000).toInt()}s / ${duration / 1000}s",
//                style = TextStyle(fontSize = 12.sp),
//                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
//            )
//        }
//    }
//}
//
//// Phát âm thanh từ file
//fun playAudio(filePath: String) {
//    val mediaPlayer = MediaPlayer().apply {
//        setDataSource(filePath)
//        prepare()
//        start()
//    }
//}

@Composable
fun ChatInputBar(
    onMessageSent: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Thay đổi cách khởi tạo MediaRecorder để đảm bảo nó được tái khởi tạo khi cần
    //var mediaRecorder: MediaRecorder? by remember { mutableStateOf(null) }
    //val outputFile = remember { File(context.getExternalFilesDir(null), "audio_message.3gp") }

//    val microphonePermission = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission(),
//        onResult = { isGranted ->
//            if (!isGranted) {
//                // Thông báo nếu không cấp quyền
//            }
//        }
//    )

//    LaunchedEffect(Unit) {
//        microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
//    }

//    // Thêm vào chức năng bắt đầu và dừng ghi âm
//    fun startRecording() {
//        // Khởi tạo lại MediaRecorder mỗi lần ghi âm bắt đầu
//        mediaRecorder = MediaRecorder().apply {
//            try {
//                reset()  // Đặt lại trạng thái nếu có bất kỳ ghi âm nào trước đó
//                setAudioSource(MediaRecorder.AudioSource.MIC)
//                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
//                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
//                setOutputFile(outputFile.absolutePath)
//                prepare()
//                start()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//
//    fun stopRecordingAndSend() {
//        try {
//            mediaRecorder?.apply {
//                stop()
//                release() // Giải phóng tài nguyên của MediaRecorder
//            }
//            // Gửi tin nhắn âm thanh
//            onMessageSent(outputFile.absolutePath, true)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//            mediaRecorder = null // Đảm bảo giải phóng tài nguyên sau khi ghi âm
//        }
//    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
//                if (isRecording) {
//                    // Dừng ghi âm và gửi tin nhắn
//                    stopRecordingAndSend()
//                } else {
//                    // Bắt đầu ghi âm
//                    startRecording()
//                }
                isRecording = !isRecording
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bug_icon),
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
            Icon(
                Icons.Default.Send,
                contentDescription = "Send Message",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Tích hợp ViewModel với ChatScreen để xử lý trạng thái
@Composable
fun ChatScreenWithViewModel(
    projectId: String,
    onNavigateBack: () -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val user by authenticationViewModel.user.observeAsState()
    val messages by chatViewModel.messages.observeAsState()
    val message by chatViewModel.message.observeAsState()


    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            authenticationViewModel.getUser("Bearer $token")
            chatViewModel.getMessage(projectId = projectId, authorization = "Bearer $token")
            val webSocketUrl = "${WEB_SOCKET}chat/$projectId/?token=${accessToken.value}"
            Log.d("WEBSOCKER", webSocketUrl)
            chatViewModel.connectToSocket(webSocketUrl)
        }
    }

    LaunchedEffect(message) {
        chatViewModel.getMessage(
            projectId = projectId,
            authorization = "Bearer ${accessToken.value}"
        )
    }

    var userId by remember {
        mutableIntStateOf(-1)
    }

    LaunchedEffect(user) {
        if (user is Resource.Success) {
            userId = user?.data?.id ?: -1
        }
    }

    if (userId == -1) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        ChatScreen(
            messages = messages?.data?.results ?: emptyList(),
            onMessageSent = { content, isAudio ->
                chatViewModel.sendMessage(MessageRequest(content))
            },
            userId = userId,
            onNavigateBack = onNavigateBack,
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


@Composable
fun DefaultPreview() {
    //ChatScreenWithViewModel()
}