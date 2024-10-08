package com.example.youmanage.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.extractMessages
import com.example.youmanage.viewmodel.AuthenticationViewModel


@Composable
fun SignUpScreen(
    navController: NavController,
    onNavigateBack: () -> Unit,
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
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        IconButton(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(horizontal = 10.dp)
                .padding(top = 36.dp),
            onClick = { onNavigateBack() }
        ) {

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(30.dp)
        ) {


            Text(
                "Create Your Account",
                color = Color.Black,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(5.dp)
            )

            Text(
                "Welcome back! Please enter your details",
                color = Color.Black,
                modifier = Modifier.alpha(0.8f)
            )

            TextFieldComponent(
                placeholderContent = "Username",
                content = username,
                onChangeValue = { username = it },
                placeholderColor = Color.Gray,
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xff9bca60),
                unfocusedIndicatorColor = Color.Gray,
                focusedTextColor = Color.Gray
            )

            TextFieldComponent(
                placeholderContent = "Email Address",
                content = email,
                onChangeValue = { email = it },
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

            PasswordTextField(
                placeholderContent = "Confirm Password",
                content = confirmPassword,
                onChangeValue = { confirmPassword = it },
                placeholderColor = Color.Gray,
                containerColor = Color.Transparent,
                focusedIndicatorColor = Color(0xff9bca60),
                unfocusedIndicatorColor = Color.Gray,
                focusedTextColor = Color.Gray
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                    "Create",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }

//            Button(
//                onClick = { /*TODO*/ },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xff9bca60)
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 30.dp)
//            ) {
//
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(15.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    Image(
//                        painter = painterResource(id = R.drawable.google_logo),
//                        modifier = Modifier.size(25.dp)
//                            .background(Color.Transparent)
//                        ,
//                        contentDescription = "Google Logo"
//                    )
//
//                    Text(
//                        "Sign up with Google",
//                        color = Color.Black,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 20.sp,
//                        modifier = Modifier.padding(vertical = 5.dp)
//                    )
//                }
//            }
        }
    }

    AlertDialog(
        title = "Alert",
        content = errorMessage, showDialog = openAlertDialog,
        onDismiss = { openAlertDialog = false },
        onConfirm = { openAlertDialog = false }
    )

}
