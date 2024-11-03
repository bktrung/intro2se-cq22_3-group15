package com.example.youmanage.screens.authetication


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.screens.AlertDialog
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.extractMessages
import com.example.youmanage.viewmodel.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateAccountScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
    onLogin: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val signUpResponse = viewModel.signUpResponse.observeAsState().value

    var openAlertDialog by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = signUpResponse) {
        if (signUpResponse is Resource.Success) {
            Log.d("SignUp", signUpResponse.data?.email.toString())
            navController.navigate("login")
        }

        if (signUpResponse is Resource.Error) {
            errorMessage = extractMessages(signUpResponse.message.toString())
            openAlertDialog = true
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        IconButton(
            onClick = {
                onNavigateBack()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 30.dp, top = 30.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "Back"
            )
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 150.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Your Account",
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Welcome back! Please enter your details",
                style = TextStyle(
                    fontSize = 16.sp
                ),
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(54.dp))

            TextFieldComponent(
                content = username,
                onChangeValue = { username = it },
                placeholderContent = "Username",
                placeholderColor = Color.Gray,
                containerColor = Color(0x1A000000)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextFieldComponent(
                content = email,
                onChangeValue = { email = it },
                placeholderContent = "Email",
                placeholderColor = Color.Gray,
                containerColor = Color(0x1A000000)
            )

            Spacer(modifier = Modifier.height(24.dp))

            PasswordTextField(
                content = password,
                onChangeValue = { password = it },
                placeholderContent = "Password",
                placeholderColor = Color.Gray,
                containerColor = Color(0x1A000000)
            )

            Spacer(modifier = Modifier.height(24.dp))

            PasswordTextField(
                content = confirmPassword,
                onChangeValue = { confirmPassword = it },
                placeholderContent = "Confirm Password",
                placeholderColor = Color.Gray,
                containerColor = Color(0x1A000000)
            )
            Spacer(modifier = Modifier.height(36.dp))

            Button(
                onClick = {
                    viewModel.signUp(
                        UserSignUp(
                            username = username,
                            email = email,
                            password1 = password,
                            password2 = confirmPassword
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 30.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Text(
                    text = "Create",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account ? ",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
                Text(
                    text = "Log in",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        onLogin()
                    }
                )
            }
        }
        AlertDialog(
            title = "Alert",
            content = errorMessage, showDialog = openAlertDialog,
            onDismiss = { openAlertDialog = false },
            onConfirm = { openAlertDialog = false }
        )
    }
}

