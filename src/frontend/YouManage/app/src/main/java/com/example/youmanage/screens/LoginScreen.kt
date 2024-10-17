package com.example.youmanage.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.utils.GoogleSignIn.googleSignIn
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel


@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    navController: NavController,
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
            Log.d("Login Success", loginResponse.data.toString())
            navController.navigate("home")
        }

        if (loginResponse is Resource.Error) {
            openNoExistDialog = true
            Log.d("Login Error", loginResponse.message.toString())
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 10.dp)
                .padding(top = 36.dp),
            onClick = { onNavigateBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Gray
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(30.dp)
        ) {

            Text(
                "Login",
                color = Color.Black,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(15.dp)
            )

            TextFieldComponent(
                placeholderContent = "Email/Username",
                content = username,
                onChangeValue = { username = it },
                placeholderColor = Color.Gray,
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xff9bca60),
                unfocusedIndicatorColor = Color.Gray,
                focusedTextColor = Color.Gray
            )

            PasswordTextField(
                placeholderContent = "Password",
                content = password,
                onChangeValue = { password = it },
                placeholderColor = Color.Gray,
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xff9bca60),
                unfocusedIndicatorColor = Color.Gray,
                focusedTextColor = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                colors = ButtonColors(
                    containerColor = Color(0xff9bca60),
                    contentColor = Color.White,
                    disabledContainerColor = Color.Black,
                    disabledContentColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {
                Text(
                    "Login",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }

            Button(
                onClick = {

                   googleSignIn(
                       context = context,
                       coroutineScope = coroutineScope,
                       credentialManager = credentialManager,
                       viewModel = viewModel
                   )

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff9bca60)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        modifier = Modifier
                            .size(25.dp)
                            .background(Color.Transparent),
                        contentDescription = "Google Logo"
                    )

                    Text(
                        "Sign in with Google",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }

            Text("Forgot Password?", color = Color.Gray)

        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldComponent(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color,
    focusedTextColor: Color,
    focusedIndicatorColor: Color,
    unfocusedIndicatorColor: Color
) {

    OutlinedTextField(
        value = content,
        onValueChange = { onChangeValue(it) },
        placeholder = {
            Text(
                text = placeholderContent,
                color = placeholderColor
            )
        },

        colors = TextFieldDefaults.textFieldColors(
            containerColor = containerColor,
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = Color.Gray,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor
        )
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    content: String,
    onChangeValue: (String) -> Unit,
    placeholderContent: String,
    placeholderColor: Color,
    containerColor: Color,
    focusedTextColor: Color,
    focusedIndicatorColor: Color,
    unfocusedIndicatorColor: Color
) {

    var passwordVisibility by remember {
        mutableStateOf(false)
    }

    val icon = if (passwordVisibility)
        painterResource(id = R.drawable.view_password_icon)
    else
        painterResource(id = R.drawable.hide_password_icon)

    OutlinedTextField(
        value = content,
        onValueChange = { onChangeValue(it) },
        placeholder = {
            Text(
                text = placeholderContent,
                color = placeholderColor
            )
        },
        trailingIcon = {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    painter = icon,
                    tint = Color.Gray,
                    contentDescription = null
                )
            }

        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = containerColor,
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = Color.Gray,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor
        ),
        visualTransformation = if (!passwordVisibility) PasswordVisualTransformation() else VisualTransformation.None
    )
}