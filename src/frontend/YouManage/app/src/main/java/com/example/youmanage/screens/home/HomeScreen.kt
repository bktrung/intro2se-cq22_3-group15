package com.example.youmanage.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.randomColor
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.example.youmanage.viewmodel.ProjectManagementViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Composable
fun HomeScreen(
    projectManagementViewModel: ProjectManagementViewModel = hiltViewModel(),
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    paddingValues: PaddingValues,
    onAddNewProject: () -> Unit,
    onViewProject: (Int) -> Unit
) {

    val textFieldColor = Color(0xFFF5F5F5)
    var searchQuery by remember { mutableStateOf("") }

    val projects by projectManagementViewModel.projects.observeAsState()
    val accessToken = authenticationViewModel.accessToken.collectAsState(initial = null)

    LaunchedEffect(accessToken.value) {
        accessToken.value?.let { token ->
            supervisorScope {
                val job1 = launch {
                    projectManagementViewModel.getProjectList(
                        authorization = "Bearer $token"
                    )
                }
                val job2 = launch {
                    authenticationViewModel.getUser("Bearer $token")
                }
            }
        }
    }

    var projectList by remember { mutableStateOf<List<Project>>(emptyList()) }

    fun onSearch(query: String) {
        val filteredList = projectList.filter { project ->
            project.name.contains(query, ignoreCase = true)
        }
        projectList = filteredList
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp, horizontal = 50.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { newValue -> searchQuery = newValue },
                placeholder = { Text("Find your project", color = MaterialTheme.colorScheme.primary) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.search_icon),
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSearch(searchQuery) // Gọi hàm tìm kiếm khi nhấn "Enter"
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done // Thay đổi hành động IME thành "Done" (Enter)
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Projects",
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    var numOfProjects = 0

                    if (projects is Resource.Success) {
                        numOfProjects = (projects as Resource.Success<Projects>).data!!.size
                    }

                    Text(
                        text = when (numOfProjects) {
                            0 -> "You have no project"
                            1 -> "You have one project"
                            else -> "You have $numOfProjects projects"
                        },
                        style = TextStyle(color = MaterialTheme.colorScheme.primary)
                    )
                }

                Button(
                    onClick = { onAddNewProject() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "+ Add", fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (projects) {
                is Resource.Success -> {
                    projectList = (projects as Resource.Success<Projects>).data!!

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(projectList.size) { item ->
                            ProjectItem(
                                title = projectList[item].name,
                                team = "",
                                backgroundColor = Color(randomColor(projectList[item].id)),
                                onViewProject = {
                                    val id = projectList[item].id
                                    if(id >= 0){
                                        onViewProject(id)
                                    }
                                }
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    Text(
                        text = (projects as Resource.Error<Projects>).message!!,
                        fontWeight = FontWeight.Bold
                    )
                }

                is Resource.Loading -> {
                    CircularProgressIndicator()
                }

                else -> {}
            }
        }
    }
}

@Composable
fun ProjectItem(
    title: String,
    team: String,
    backgroundColor: Color,
    onViewProject: (Int) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1.3f)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = team,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.6f)
                    )
                )
            }
            Image(
                painter = painterResource(id = R.drawable.img_book),
                contentDescription = "Project Image",
                modifier = Modifier
                    .size(100.dp)
                    .rotate(-45f)
            )
        }

        Button(
            onClick = { onViewProject(-1) },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(vertical = 16.dp)
                .fillMaxWidth(0.7f),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF251034))
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                Text(text = "View Project", color = Color.White)
                Spacer(modifier = Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Color.Gray,
                            shape = CircleShape
                        )
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "➜", modifier = Modifier)
                }
            }
        }
    }
}