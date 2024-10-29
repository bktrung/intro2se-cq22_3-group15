package com.example.youmanage.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.youmanage.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen2(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Mũi tên chỉ về bên trái
        Image(
            painter = painterResource(id = R.drawable.left),
            contentDescription = "Back",
            modifier = Modifier
                .size(24.dp)
                .clickable { navController.navigate("manage_task") } // Chuyển hướng đến ManageTaskScreen
        )

        // Nội dung chính
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tiêu đề
            Text(
                text = "Login",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp)) // Khoảng cách lớn hơn giữa tiêu đề và các trường nhập liệu

            // Màu nền cho TextField
            val textFieldBackgroundColor = Color(0x1A000000) // Màu nền #0000001A

            // Trường nhập tài khoản
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username/Email", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = textFieldBackgroundColor,
                    unfocusedContainerColor = textFieldBackgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )

            Spacer(modifier = Modifier.height(30.dp)) // Khoảng cách lớn hơn giữa các trường nhập liệu

            // Trường nhập mật khẩu
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.view_password_icon else R.drawable.hide_password_icon),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.clickable { passwordVisible = !passwordVisible },
                        tint = Color(0xFFB0B0B0) // Màu xám nhạt dễ nhìn
                    )
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = textFieldBackgroundColor,
                    unfocusedContainerColor = textFieldBackgroundColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Khoảng cách giữa trường mật khẩu và link quên mật khẩu

            // Link quên mật khẩu
            Text(
                text = "Forgot password?",
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 30.dp)
            )

            Spacer(modifier = Modifier.height(40.dp)) // Khoảng cách lớn hơn trước nút đăng nhập

            // Nút đăng nhập
            Button(
                onClick = { /* TODO: Thêm xử lý đăng nhập */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(
                    text = "Login",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hoặc
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
                Text(
                    text = "OR",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(modifier = Modifier.weight(1f), color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút đăng nhập với Google
            IconButton(
                onClick = { /* TODO: Thêm xử lý đăng nhập bằng Google */ },
                modifier = Modifier
                    .size(72.dp)
                    .background(Color.White, shape = RoundedCornerShape(36.dp))
                    .border(1.dp, Color.Gray, shape = RoundedCornerShape(36.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Login with Google",
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val navController = rememberNavController()
    LoginScreen2(navController)
}
