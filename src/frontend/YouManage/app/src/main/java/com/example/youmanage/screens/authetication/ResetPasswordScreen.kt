package com.example.youmanage.screens.authetication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.ChangePasswordRequest
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.screens.components.PasswordTextField
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel

@Composable
fun ResetPasswordScreen(
    onNavigateBack: () -> Unit,
    onChangePasswordSuccess: () -> Unit,
    resetToken: String,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {
    var openErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val response = authenticationViewModel.message.observeAsState().value

    LaunchedEffect(response) {
        if (response is Resource.Success) {
            onChangePasswordSuccess()
        } else if(response is Resource.Error){
            errorMessage = response.message.toString()
            Log.d("Reset Password", response.message.toString())
            openErrorDialog =  true
        }
    }
    Log.d("Reset", resetToken)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                onNavigateBack()
            },
            modifier = Modifier
                .padding(start = 20.dp, top = 30.dp)
                .align(Alignment.TopStart)

        ) {
            Icon(
                painter = painterResource(id = R.drawable.back_arrow_icon),
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.reset_password_img),
                contentDescription = "Reset Password",
                modifier = Modifier.size(250.dp)

            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Reset Password",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(50.dp))

            PasswordTextField(
                content = newPassword,
                onChangeValue = { newPassword = it },
                placeholderContent = "Password",
                placeholderColor = Color.Gray,
                containerColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(20.dp))


            PasswordTextField(
                content = confirmPassword,
                onChangeValue = { confirmPassword = it },
                placeholderContent = "Confirm Password",
                placeholderColor = Color.Gray,
                containerColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (newPassword != confirmPassword) {
                        errorMessage = "Confirmation password does not match."
                        openErrorDialog = true
                    } else {
                        authenticationViewModel.changePassword(
                            ChangePasswordRequest(
                                newPassword = newPassword,
                                confirmPassword = confirmPassword,
                                resetToken = resetToken
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
            {
                Text(
                    "Reset Password",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 5.dp)
                )
            }
        }
    }

    if(openErrorDialog){
        ErrorDialog(
            title = "Error",
            showDialog = true,
            content = errorMessage,
            onDismiss = { openErrorDialog = false },
            onConfirm = { openErrorDialog = false }
        )
    }

}