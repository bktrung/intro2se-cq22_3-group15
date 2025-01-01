package com.example.youmanage.screens.project_management

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.screens.components.AddMemberDialog
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.PieChart
import com.example.youmanage.utils.Constants.WEB_SOCKET
import com.example.youmanage.utils.HandleOutProjectWebSocket
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.randomAvatar
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import com.example.youmanage.viewmodel.projectmanagement.ProjectDetailViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Composable
fun ProjectDetailScreen(
    id: Int,
    onNavigateBack: () -> Unit,
    onClickMenu: () -> Unit,
    onDisableAction: () -> Unit,
    onUpdateProject: () -> Unit,
    onMemberProfile: (Int) -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectDetailViewModel: ProjectDetailViewModel = hiltViewModel()
) {

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)

    val project by projectDetailViewModel.project.observeAsState()
    val pieChartInputList by projectDetailViewModel.progress.observeAsState()
    val addMemberResponse by projectDetailViewModel.addMemberResponse.observeAsState()
    val removeMemberResponse by projectDetailViewModel.removeMemberResponse.observeAsState()
    val isHost by projectDetailViewModel.isHost.observeAsState()

    val memberSocket by projectDetailViewModel.memberSocket.observeAsState()
    val projectSocket by projectDetailViewModel.projectSocket.observeAsState()
    val taskSocket by projectDetailViewModel.taskSocket.observeAsState()

    val user by authenticationViewModel.user.observeAsState()

    var showAddMemberDialog by rememberSaveable { mutableStateOf(false) }
    var showRemoveAlertDialog by rememberSaveable { mutableStateOf(false) }
    var showAddAlertDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var isRemove by remember { mutableStateOf(false) }
    var isAdd by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            val authorization = "Bearer $token"

            // Lệnh gọi API và WebSocket được thực hiện tuần tự hoặc song song có kiểm soát
            try {
                // Gọi API trước
                supervisorScope {
                    // Khởi chạy các coroutine song song cho các API gọi
                    val apiJobs = listOf(
                        launch { projectDetailViewModel.getProject(id.toString(), authorization) },
                        launch { projectDetailViewModel.getProgressTrack(id.toString(), authorization) },
                        launch { projectDetailViewModel.isHost(id.toString(), authorization) }
                    )

                    // Đợi tất cả API hoàn thành trước khi tiếp tục
                    apiJobs.joinAll()

                    // Sau khi các API hoàn thành, kết nối WebSocket
                    val webSocketUrl = "${WEB_SOCKET}project/$id/"
                    val webSocketJobs = listOf(
                        launch { authenticationViewModel.getUser("Bearer $token") },
                        launch { projectDetailViewModel.connectToAllWebSockets(url = webSocketUrl) },

                    )
                    // Đợi tất cả các WebSocket kết nối
                    webSocketJobs.joinAll()
                }
            } catch (e: Exception) {
                Log.e("ProjectDetailScreen", "Error occurred: ${e.message}")
            }
        }
    }

    val memberStage = listOf(
        "member_added",
        "member_removed"
    )

    val taskStage = listOf(
        "task_created",
        "task_updated",
        "task_deleted"
    )

    LaunchedEffect(
        addMemberResponse,
        removeMemberResponse,
        projectSocket,
        memberSocket
    ) {
        // Xử lý khi thêm thành viên
        if (addMemberResponse is Resource.Error && isAdd) {
            showAddAlertDialog = true
        }

        if (addMemberResponse is Resource.Success) {
            supervisorScope {
                projectDetailViewModel.getProject(
                    projectId = id.toString(),
                    authorization = "Bearer ${accessToken.value}"
                )
            }
        }

        // Xử lý khi xóa thành viên
        if (removeMemberResponse is Resource.Error && isRemove) {
            showRemoveAlertDialog = true
        }

        if (removeMemberResponse is Resource.Success) {
            projectDetailViewModel.getProject(
                projectId = id.toString(),
                authorization = "Bearer ${accessToken.value}"
            )
        }

        if (projectSocket is Resource.Success &&
            projectSocket?.data?.type == "project_updated" &&
            projectSocket?.data?.content?.id == id
        ) {
            projectDetailViewModel.getProject(
                id.toString(),
                "Bearer ${accessToken.value}"
            )
        }

        if (memberSocket is Resource.Success &&
            memberStage.contains(memberSocket?.data?.type) &&
            projectSocket?.data?.content?.id == id
        ) {
            projectDetailViewModel.getProject(
                id.toString(),
                "Bearer ${accessToken.value}")
        }
    }

    HandleOutProjectWebSocket(
        memberSocket = memberSocket,
        projectSocket = projectSocket,
        user = user,
        projectId = id.toString(),
        onDisableAction = onDisableAction
    )

    LaunchedEffect(taskSocket) {
        if (taskSocket is Resource.Success &&
            taskStage.contains(taskSocket?.data?.type) &&
            projectSocket?.data?.content?.id == id
        ) {
            supervisorScope {
                launch {
                    projectDetailViewModel.getProgressTrack(
                        projectId = id.toString(),
                        authorization = "Bearer ${accessToken.value}"
                    )
                }
            }
        }
    }

    Log.d("Access Token", "${accessToken.value}")
    Log.d("Project ID", "$id")

    if (project is Resource.Success) {

        var memberId by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopBar(
                    "Project Detail",
                    trailing = {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if(isHost == true){
                                    onUpdateProject()
                                } else{
                                    Toast.makeText(context, "You are not the host of this project", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Update"
                                )

                            }
                            IconButton(onClick = { onClickMenu() }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }

                    },
                    color = Color.Transparent,
                    onNavigateBack = { onNavigateBack() }
                )
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(
                    bottom = WindowInsets.systemBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                )

        ) { paddingValues ->

            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Project Name",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = project?.data?.name.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (pieChartInputList != null && pieChartInputList?.isNotEmpty() == true) {
                        PieChart(
                            input = pieChartInputList ?: emptyList(),
                            modifier = Modifier.padding(36.dp)
                        )
                    }

                    DescriptionSection(
                        description = project?.data?.description.toString()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Due date",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = project?.data?.dueDate.toString(),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    MembersSection(
                        onAddNewMember = {
                            if(isHost == true){
                                showAddMemberDialog = true
                            } else{
                                Toast.makeText(context, "You are not the host of this project", Toast.LENGTH_SHORT).show()
                            }
                        },

                        onDeleteMember = {
                            memberId = it
                            showDeleteDialog = true
                        },
                        members = project?.data?.members ?: emptyList(),
                        onMemberProfile = onMemberProfile,
                        hostId = project?.data?.host?.id ?: -1,
                        isHost = user?.data?.id == project?.data?.host?.id
                    )
                }
            }
        }

        AddMemberDialog(
            title = "Choose Member",
            showDialog = showAddMemberDialog,
            onDismiss = {
                showAddMemberDialog = false
            },
            onConfirm = {
                accessToken.value?.let { token ->
                    projectDetailViewModel.addMember(
                        projectId = id.toString(),
                        username = it.username,
                        authorization = "Bearer $token"
                    )
                }

                isAdd = true
                showAddMemberDialog = false
            },
        )

        AlertDialog(
            title = "Something wrong",
            content = addMemberResponse?.message.toString(),
            showDialog = showAddAlertDialog,
            onDismiss = {
                showAddAlertDialog = false
            },
            onConfirm = {
                showAddAlertDialog = false
            })

        AlertDialog(
            title = "Something wrong",
            content = removeMemberResponse?.message.toString(),
            showDialog = showRemoveAlertDialog,
            onDismiss = {
                showRemoveAlertDialog = false
            },
            onConfirm = {
                showRemoveAlertDialog = false
            })

        AlertDialog(
            title = "Remove Member?",
            content = "Are you sure you want to remove this member?",
            showDialog = showDeleteDialog,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                accessToken.value?.let { token ->
                    projectDetailViewModel.removeMember(
                        projectId = id.toString(),
                        memberId = memberId,
                        authorization = "Bearer $token"
                    )
                }

                isRemove = true
                showDeleteDialog = false
            }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        LaunchedEffect(project) {
            delay(500)
        }
    }
}


@Composable
fun TopBar(
    title: String,
    color: Color,
    haveLeading: Boolean = true,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    onNavigateBack: () -> Unit = {}

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if(haveLeading){
            IconButton(onClick = { onNavigateBack() }) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow_icon),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Box(modifier = Modifier.size(24.dp))
        }

        Text(
            text = title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        trailing?.invoke()
    }
}

@Composable
fun DescriptionSection(
    description: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
    ) {
        Text(
            text = "Description",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun MembersSection(
    members: List<User> = emptyList(),
    onAddNewMember: () -> Unit = {},
    onDeleteMember: (String) -> Unit = {},
    onMemberProfile: (Int) -> Unit,
    hostId: Int = -1,
    isHost: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(horizontal = 36.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Members",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { onAddNewMember() },
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
            ) {
                Text(text = "+ Add", fontSize = 16.sp)
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color.Black.copy(alpha = 0.1f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(members.size) { index ->
                    MemberItem(
                        MemberItem(
                            username = members[index].username ?: "Unknown",
                            backgroundColor = Color.Transparent,
                            avatar = randomAvatar(index = members[index].id),
                        ),
                        onDelete = {
                            onDeleteMember(members[index].id.toString())
                        },
                        onClick = { onMemberProfile(members[index].id) },
                        modifier = Modifier.fillMaxWidth(0.7f),
                        hostId = hostId,
                        userId = members[index].id,
                        isHost = isHost
                    )
                }
            }
        }
    }
}