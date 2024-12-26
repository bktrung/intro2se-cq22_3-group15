package com.example.youmanage.screens.authetication


import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.screens.components.PasswordTextField
import com.example.youmanage.screens.components.TextFieldComponent
import com.example.youmanage.ui.theme.fontFamily
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.extractMessages
import com.example.youmanage.viewmodel.AuthenticationViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateAccountScreen(
    onNavigateBack: () -> Unit,
    onLogin: () -> Unit,
    onCreateSuccess: (String) -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val signUpResponse = viewModel.signUpResponse.observeAsState().value

    var openAlertDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(key1 = signUpResponse) {
        when (signUpResponse) {
            is Resource.Success -> {
                onCreateSuccess(signUpResponse.data?.email.toString())
            }

            is Resource.Error -> {
                errorMessage = extractMessages(signUpResponse.message.toString())
                openAlertDialog = true
            }

            is Resource.Loading -> {
                isLoading = true
            }
             else ->{
                 //isLoading = true
             }
        }
    }

    Log.d("IN create account", signUpResponse.toString())

    if(isLoading){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
    }
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            IconButton(
                onClick = { onNavigateBack() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 30.dp, top = 30.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_arrow_icon),
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create Your Account",
                    fontFamily = fontFamily,
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Welcome back! Please enter your details",
                    style = TextStyle(
                        fontSize = 16.sp
                    ),
                    fontFamily = fontFamily,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(54.dp))

                TextFieldComponent(
                    content = username,
                    onChangeValue = { username = it },
                    placeholderContent = "Username",
                    placeholderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextFieldComponent(
                    content = email,
                    onChangeValue = { email = it },
                    placeholderContent = "Email",
                    placeholderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(24.dp))

                PasswordTextField(
                    content = password,
                    onChangeValue = { password = it },
                    placeholderContent = "Password",
                    placeholderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(24.dp))

                PasswordTextField(
                    content = confirmPassword,
                    onChangeValue = { confirmPassword = it },
                    placeholderContent = "Confirm Password",
                    placeholderColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface
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
                        containerColor = MaterialTheme.colorScheme.primary,
                    )
                ) {
                    Text(
                        text = "Create",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = fontFamily,
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
                        fontFamily = fontFamily,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Log in",
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = fontFamily,
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
}

