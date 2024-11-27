package com.example.youmanage.screens.project_management

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.screens.DatePickerModal
import com.example.youmanage.screens.LeadingTextFieldComponent
import com.example.youmanage.utils.randomVibrantLightColor
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel

data class MemberItem(
    val username: String,
    val backgroundColor: Color,
    val avatar: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddProjectScreen(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {

    var showDatePicker by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }

    var members by remember { mutableStateOf(listOf<MemberItem>()) }

    val textFieldColor = Color(0xFFF5F5F5)

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var dueDate by rememberSaveable { mutableStateOf("") }

    val access = authenticationViewModel.accessToken.collectAsState(initial = null)

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
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    onNavigateBack()
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow_icon),
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(32.dp))
                Text(
                    "Create Project",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        "Project Title",
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
                        "Due date",
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
                            value = dueDate,
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
                                        showDatePicker = true
                                    }
                                )
                            },
                            placeholder = {
                                Text(
                                    text = "Enter due date",
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

//                    Text(
//                        "Members",
//                        color = Color.Black,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp
//                    )
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//
//                        Button(
//                            onClick = { showAddMemberDialog = true },
//                            colors = ButtonDefaults.buttonColors(
//                                contentColor = Color.Black,
//                                containerColor = Color(0xFFF5F5F5)
//                            ),
//                            modifier = Modifier.size(50.dp),
//                            shape = RoundedCornerShape(10.dp)
//
//                        ) {
//                            Text(
//                                "+",
//                                fontSize = 20.sp,
//                                textAlign = TextAlign.Center,
//                                fontWeight = FontWeight.Bold,
//
//                                )
//                        }
//
//                        LazyRow(
//                            modifier = Modifier.weight(1f),
//                            horizontalArrangement = Arrangement.spacedBy(10.dp)
//                        ) {
//
//                            items(members.size) { index ->
//                                MemberItem(
//                                    members[index],
//                                    onDelete = {
//                                        members = members.filter { i -> i.username != it }
//                                    }
//                                )
//                            }
//                        }
//
//                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            Log.d("AddProjectScreen", "AddProjectScreen: $access")
                            projectManagementViewModel.createProject(
                                project = ProjectCreate(
                                    description = description,
                                    dueDate = dueDate,
                                    name = title
                                ),
                                authorization = "Bearer ${access.value}"
                            )
                            onNavigateBack()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()

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
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = {
                dueDate = it
            },
            onDismiss = {
                showDatePicker = false
            })
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            title = "Add Member",
            showDialog = true,
            onDismiss = {
                showAddMemberDialog = false
            },
            onConfirm = {
                if (it.username != "") members = members + it
                showAddMemberDialog = false
            }
        )
    }
}

@Composable
fun AddMemberDialog(
    title: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (MemberItem) -> Unit
) {
    if (showDialog) {

        var username by remember {
            mutableStateOf("")
        }

        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    ),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),

                ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            "Username",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )

                        LeadingTextFieldComponent(
                            content = username,
                            onChangeValue = { username = it },
                            placeholderContent = "Username",
                            placeholderColor = Color.Gray,
                            containerColor = Color(0x1A000000),
                            icon = R.drawable.member_icon
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            onConfirm(
                                MemberItem(
                                    username,
                                    randomVibrantLightColor(),
                                    R.drawable.avatar
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Text(
                            "Add",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun MemberItem(
    member: MemberItem,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(5.dp))
            .background(member.backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = member.avatar
                ),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Text(text = member.username, color = Color.Black, fontWeight = FontWeight.Medium)

            Spacer(modifier = Modifier.width(20.dp))

            Icon(
                painter = painterResource(id = R.drawable.delete_icon),
                contentDescription = "Delete",
                modifier = Modifier.clickable {
                    onDelete(member.username)
                }
            )

        }
    }
}
