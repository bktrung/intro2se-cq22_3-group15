package com.example.youmanage.screens.task_management

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.screens.ChooseItemDialog
import com.example.youmanage.screens.DatePickerModal
import com.example.youmanage.screens.LeadingTextFieldComponent
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import com.example.youmanage.viewmodel.TaskManagementViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTaskScreen(
    navHostController: NavHostController,
    projectId: String,
    onCreateTask: () -> Unit,
    taskManagementViewModel: TaskManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel()
) {

    val members by projectManagementViewModel.members.observeAsState()

    val task by taskManagementViewModel.task.observeAsState()

    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)

    LaunchedEffect(task) {
        if (task is Resource.Success) {
            onCreateTask()
        }
    }

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            projectManagementViewModel.getMembers(projectId, "Bearer $token")
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val textFieldColor = Color(0xFFF5F5F5)

    var showChooseMember by remember {
        mutableStateOf(false)
    }

    var isTime by rememberSaveable { mutableIntStateOf(0) }

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }

    var assignedMemberId by remember {
        mutableIntStateOf(-1)
    }
    var assignedMember by remember {
        mutableStateOf("Unassigned")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {

                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow_icon),
                        contentDescription = "",
                        tint = Color.Black
                    )
                }


                Text(
                    "Create Task",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(30.dp))


            }

            Spacer(modifier = Modifier.height(10.dp))

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Task Title",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    LeadingTextFieldComponent(
                        content = title,
                        onChangeValue = { title = it },
                        placeholderContent = "Enter project title",
                        placeholderColor = Color.Gray,
                        containerColor = textFieldColor,
                        icon = R.drawable.project_title_icon
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Description",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = textFieldColor,
                            unfocusedContainerColor = textFieldColor,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.description_icon),
                                contentDescription = "",
                                tint = Color.Black
                            )
                        },
                        placeholder = {
                            Text(
                                "Enter project description",
                                color = Color.Gray
                            )
                        },
                        maxLines = Int.MAX_VALUE,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Start date",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Box(
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }
                    ) {
                        TextField(
                            value = startDate,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.calendar_icon
                                    ),
                                    contentDescription = "",
                                    modifier = Modifier.clickable {
                                        isTime = 1
                                        showDatePicker = true
                                    }
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Enter start date",
                                    color = Color.Gray
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = textFieldColor,
                                unfocusedContainerColor = textFieldColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "End date",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Box(
                        modifier = Modifier.clickable {
                            showDatePicker = true
                        }
                    ) {
                        TextField(
                            value = endDate,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.calendar_icon
                                    ),
                                    contentDescription = "",
                                    modifier = Modifier.clickable {
                                        isTime = 2
                                        showDatePicker = true
                                    }
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Enter end date",
                                    color = Color.Gray
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = textFieldColor,
                                unfocusedContainerColor = textFieldColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                }
                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Assign to",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Box(
                        modifier = Modifier.clickable {
                            showChooseMember = true
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(textFieldColor)
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.avatar),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                assignedMember,
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        taskManagementViewModel.createTask(
                            projectId = projectId,
                            TaskCreate(
                                title = title,
                                description = description,
                                startDate = startDate,
                                endDate = endDate,
                                assigneeId = 1
                            ),
                            authorization = "Bearer ${accessToken.value}"
                        )

                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)

                ) {
                    Text(
                        "Create",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                }

            }
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = {
                if (isTime == 1) {
                    startDate = it
                } else if (isTime == 2) {
                    endDate = it
                }
                isTime = 0
            },
            onDismiss = {
                showDatePicker = false
                isTime = 0
            })
    }

    ChooseItemDialog(
        title = "Choose Member",
        showDialog = showChooseMember,
        items = if (members is Resource.Success) members?.data!! else emptyList(),
        displayText = { it.username },
        onDismiss = { showChooseMember = false },
        onConfirm = { user ->
            assignedMemberId = user.id
            assignedMember = user.username
            showChooseMember = false
        }
    )

}

