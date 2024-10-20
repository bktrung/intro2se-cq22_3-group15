package com.example.youmanage.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen() {
    val navController = rememberNavController()
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F0F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { newValue -> searchQuery = newValue },
                    placeholder = { Text("Find your project", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.img_search),
                            contentDescription = "Search"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFD9D9D9),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sử dụng Row để đặt FloatingActionButton ngang với Projects
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Projects",
                        style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                    FloatingActionButton(
                        onClick = { /* Thêm hành động cho nút thêm */ },
                        containerColor = Color.Black,
                        contentColor = Color.White,
                        modifier = Modifier.size(48.dp) // Điều chỉnh kích thước cho phù hợp
                    ) {
                        Text(text = "+ Add", style = TextStyle(fontSize = 16.sp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "You have 6 projects", style = TextStyle(color = Color.Black))

                Spacer(modifier = Modifier.height(16.dp))

                // Danh sách dự án với màu nền khác nhau
                ProjectItem("Sciences", "Derat Team", backgroundColor = Color(0xFF75FB6B))
                Spacer(modifier = Modifier.height(16.dp))
                ProjectItem("Information", "Secret Team", backgroundColor = Color(0xFFFFCC99))
            }
        }
    }
}


@Composable
fun ProjectItem(title: String, team: String, backgroundColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(262.dp)
            .background(backgroundColor, shape = RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold))
                Text(text = team, style = TextStyle(fontSize = 16.sp, color = Color.Gray))
            }
            Image(
                painter = painterResource(id = R.drawable.img_book),
                contentDescription = "Project Image",
                modifier = Modifier.size(100.dp)
            )
        }

        Button(
            onClick = { /* Thêm hành động cho nút View Project */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .fillMaxWidth(0.8f)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B0082))
        ) {
            Text(text = "View Project", color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            // Bọc mũi tên trong một hình tròn
            Box(
                modifier = Modifier
                    .size(32.dp) // Kích thước hình tròn
                    .background(Color.Gray, shape = RoundedCornerShape(50)) // Màu nền và hình dạng tròn
                    .padding(4.dp), // Khoảng cách giữa mũi tên và viền tròn
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.img_arrow),
                    contentDescription = "Arrow Icon",
                    tint = Color.White // Đặt màu cho mũi tên
                )
            }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.img_home), contentDescription = "Home") },
            label = { Text("Home") },
            selected = false,
            onClick = { /* Điều hướng đến màn hình Home */ }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.img_task), contentDescription = "Task") },
            label = { Text("Task") },
            selected = false,
            onClick = { /* Điều hướng đến màn hình Task */ }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.img_add), contentDescription = "Add") },
            label = { Text("Add") },
            selected = false,
            onClick = { /* Điều hướng đến màn hình Add */ }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.img_calendar), contentDescription = "Calendar") },
            label = { Text("Calendar") },
            selected = false,
            onClick = { /* Điều hướng đến màn hình Calendar */ }
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.img_person), contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { /* Điều hướng đến màn hình Profile */ }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewProjectScreen() {
    ProjectScreen()
}