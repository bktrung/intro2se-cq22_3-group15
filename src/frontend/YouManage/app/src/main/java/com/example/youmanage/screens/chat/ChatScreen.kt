package com.example.youmanage.screens.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.youmanage.R
import com.example.youmanage.data.remote.chat.Message
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.utils.Constants.BASE_URL
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.randomAvatar
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.projectmanagement.ChatViewModel
import com.example.youmanage.viewmodel.TraceInProjectViewModel
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Preview(showBackground = true)
@Composable
fun ChatScreen(
    messages: List<Message> = emptyList(),
    onMessageSent: (String, Boolean) -> Unit = { _, _ -> },
    onImageSent: (String) -> Unit = { _ -> },
    userId: Int = 1,
    onNavigateBack: () -> Unit = {},
    loadPrevMessage: () -> Unit = {},
    loadNextMessage: () -> Unit = {},

    ) {

    val isFirstTime = remember { mutableStateOf(true) }
    val listState = rememberLazyListState()// Trạng thái để kiểm tra lần đầu vào chat


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(
                bottom = WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),

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
                onImageSent = onImageSent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
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
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.background
                        )
                    )
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {

                LaunchedEffect(messages) {
                    if (isFirstTime.value && messages.isNotEmpty()) {
                        listState.animateScrollToItem(index = messages.size - 1)
                        isFirstTime.value = false
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    item {
                        LaunchedEffect(Unit) {
                            loadNextMessage()
                        }
                    }

                    items(messages.size) { index ->
                        Column {
                            MessageBubble(
                                message = messages[index],
                                isSentByUser = messages[index].author.id == userId,
                                username = messages[index].author.username ?: "Unknown",
                                avatarUrl = messages[index].author.username ?: "Unknown"
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
    message: Message = Message(
        User(),
        "Yes", "", 0, 0, ""
    ),
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
                Image(
                    painter = painterResource(randomAvatar(message?.author?.id ?: -1)),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
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
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                    )
                }

                TextMessageBubble(
                    content = message.content,
                    isSentByUser = isSentByUser,
                    imagePath = message.imageUrl
                )
            }

            if (isSentByUser) {
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun Avatar(image: Int, contentDescription: String) {
    Box(
        modifier = Modifier
            .size(40.dp) // Size of the avatar
            .clip(CircleShape) // Makes the avatar circular
            .background(AppColors.BackgroundColor) // Background color inside the circle
            .border(2.dp, AppColors.BackgroundColor, CircleShape) // Border color and shape
            .padding(8.dp) // Padding around the icon
    ) {
        Image(
            painter = painterResource(image),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
        )
    }
}


@Composable
fun TextMessageBubble(content: String, imagePath: String?, isSentByUser: Boolean) {
    val fullImageUrl = imagePath?.let { "$BASE_URL$it" } // Xây dựng URL nếu imagePath không null

    // Hiển thị văn bản
    if (content.isNotBlank()) {
        MessageContent(
            content = content,
            isSentByUser = isSentByUser
        )
    }
    // Hiển thị hình ảnh nếu có
    if (fullImageUrl != null) {
        MessageImage(imageUrl = fullImageUrl)
    }
}

@Composable
fun MessageContent(content: String, isSentByUser: Boolean) {
    Column(
        modifier = Modifier
            .background(
                color = if (isSentByUser) AppColors.SentMessageColor else AppColors.ReceivedMessageColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.Start // Điều chỉnh alignment cho nội dung
    ) {
        Text(
            text = content,
            color = AppColors.TextColor,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        )
    }
}

@Composable
fun MessageImage(imageUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = imageUrl,
                placeholder = painterResource(R.drawable.find_user_img), // Hình placeholder
                error = painterResource(R.drawable.error_img) // Hình lỗi
            ),
            contentDescription = null,
            modifier = Modifier
                .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp) // Kích thước tối đa
                .clip(RoundedCornerShape(8.dp)) // Bo góc cho hình ảnh
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Inside // Giữ nguyên tỷ lệ gốc
        )
    }
}

@Composable
fun ChatInputBar(
    onMessageSent: (String, Boolean) -> Unit,
    onImageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by remember { mutableStateOf("") }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                val base64Image = convertImageToBase64(context, uri)
                if (base64Image != null) {
                    onImageSent(base64Image)
                }
            }
        }
    }


    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = {
                // Xử lý logic gửi ảnh ở đây
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = "image/*"
                }
                launcher.launch(intent)
            },
            modifier = Modifier.padding(8.dp),
        ) {

            Icon(
                painter = painterResource(id = R.drawable.icon_image),
                contentDescription = "Send Image",
                tint = MaterialTheme.colorScheme.primary,
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
    onDisableAction: () -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
    traceInProjectViewModel: TraceInProjectViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)
    val user by authenticationViewModel.user.observeAsState()
    val messages by chatViewModel.messages.observeAsState()
    val message by chatViewModel.messageSocket.observeAsState()

    val shouldDisableAction by traceInProjectViewModel.observeCombinedLiveData(projectId).observeAsState(false)

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val url = "${WEB_SOCKET}project/${projectId}"
            val webSocketUrl = "${WEB_SOCKET}chat/$projectId/?token=$token"

            supervisorScope {
                val job1 = launch {
                    traceInProjectViewModel.connectToWebSocketAndUser(token, url)
                }

                val job2 = launch {
                    try {
                        authenticationViewModel.getUser("Bearer $token")
                    } catch (e: Exception) {
                        Log.e("Authentication", "Error: ${e.message}")
                    }
                }

                val job3 = launch {
                    try {
                        chatViewModel.getMessages(
                            projectId = projectId,
                            authorization = "Bearer $token"
                        )
                        Log.d("Call getMessage", chatViewModel.messages.value.toString())
                    } catch (e: Exception) {
                        Log.e("ChatMessages", "Error: ${e.message}")
                    }
                }

                val job4 = launch {
                    try {
                        Log.d("WEB SOCKET", webSocketUrl)
                        chatViewModel.connectToSocket(webSocketUrl)
                    } catch (e: Exception) {
                        Log.e("ChatWebSocket", "Error: ${e.message}")
                    }
                }

                joinAll(job1, job2, job3, job4)
            }
        }
    }

    LaunchedEffect(shouldDisableAction) {
        if(shouldDisableAction){
            onDisableAction()
        }
    }

    LaunchedEffect(message) {
        supervisorScope {
            launch {
                chatViewModel.getNewSocketMessage(
                    projectId = projectId,
                    authorization = "Bearer ${accessToken.value}"
                )
            }
        }
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
            messages = messages?.reversed() ?: emptyList(),
            onMessageSent = { content, isAudio ->
                chatViewModel.sendMessage(MessageRequest(message = content, image = null))
            },
            onImageSent = { base64Image ->
                chatViewModel.sendMessage(MessageRequest(message = null, image = base64Image))
            },
            userId = userId,
            onNavigateBack = onNavigateBack,
            loadPrevMessage = {
                chatViewModel.getPreviousMessages(
                    projectId = projectId,
                    authorization = "Bearer ${accessToken.value}"
                )
            },
            loadNextMessage = {
                chatViewModel.getNextMessages(
                    projectId = projectId,
                    authorization = "Bearer ${accessToken.value}"
                )
            }
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

// Hàm chuyển đổi ảnh sang Base64
fun convertImageToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        if (bytes != null) {
            val base64String = Base64.encodeToString(bytes, Base64.DEFAULT)
            "data:image/png;base64,$base64String"
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
