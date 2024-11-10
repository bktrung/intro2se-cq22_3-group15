package com.example.youmanage.screens.authetication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.screens.AlertDialog
import com.example.youmanage.screens.PasswordTextField
import com.example.youmanage.screens.TextFieldComponent
import com.example.youmanage.utils.Constants.ACCESS_TOKEN_KEY
import com.example.youmanage.utils.Constants.REFRESH_TOKEN_KEY
import com.example.youmanage.utils.GoogleSignIn.googleSignIn
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel

@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthenticationViewModel = hiltViewModel()
) {
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var openFillInDialog by remember { mutableStateOf(false) }
    var openNoExistDialog by remember { mutableStateOf(false) }

    val loginResponse = viewModel.logInResponse.observeAsState().value

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    LaunchedEffect(loginResponse) {
        if (loginResponse is Resource.Success) {
            loginResponse.data?.let {
                viewModel.saveToken(
                    loginResponse.data.access,
                    loginResponse.data.refresh,
                    ACCESS_TOKEN_KEY,
                    REFRESH_TOKEN_KEY
                )
            }
            onLoginSuccess()
        } else if (loginResponse is Resource.Error) {
            openNoExistDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
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
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Login",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))


            val textFieldBackgroundColor = Color(0x1A000000)


            TextFieldComponent(
                content = username,
                onChangeValue = { username = it },
                placeholderContent = "Email/Username",
                placeholderColor = Color.Gray,
                containerColor = textFieldBackgroundColor,
            )


            Spacer(modifier = Modifier.height(30.dp))


            PasswordTextField(
                content = password,
                onChangeValue = { password = it },
                placeholderContent = "Password",
                placeholderColor = Color.Gray,
                containerColor = textFieldBackgroundColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Forgot password?",
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 30.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Log in Button
            Button(
                onClick = {
                    if (username.isEmpty() || password.isEmpty()) {
                        openFillInDialog = true
                    } else {
                        viewModel.logIn(
                            UserLogIn(
                                username = username,
                                password = password
                            )
                        )
                    }
                },
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

            // OR
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 30.dp),
                    color = Color.Gray
                )
                Text(
                    text = "OR",
                    color = Color.Gray,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 30.dp),
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log in with Google
            IconButton(
                onClick = {
                    googleSignIn(
                        context = context,
                        coroutineScope = coroutineScope,
                        credentialManager = credentialManager,
                        viewModel = viewModel
                    )
                },
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.White, CircleShape)
                    .border(1.dp, Color.Gray, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Sign in with Google",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            }

            AlertDialog(
                title = "Alert",
                content = "Please fill in all fields!",
                showDialog = openFillInDialog,
                onDismiss = { openFillInDialog = false },
                onConfirm = { openFillInDialog = false }
            )

            AlertDialog(
                title = "Alert",
                content = "Username or password is incorrect!",
                showDialog = openNoExistDialog,
                onDismiss = { openNoExistDialog = false },
                onConfirm = { openNoExistDialog = false })
        }
    }
}


