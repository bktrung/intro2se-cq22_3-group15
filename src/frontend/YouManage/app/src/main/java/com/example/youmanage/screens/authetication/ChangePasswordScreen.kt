package com.example.youmanage.screens.authetication

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.R
import com.example.youmanage.data.remote.authentication.ChangePassword
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.screens.components.PasswordTextField
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    onChangePasswordSuccess: () -> Unit,
    authenticationViewModel: AuthenticationViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current

    var openErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val accessToken = authenticationViewModel.accessToken.collectAsState(null)

    val response = authenticationViewModel.message.observeAsState().value

    val context = LocalContext.current

    LaunchedEffect(response) {
        if (response is Resource.Success) {
            Toast.makeText(context, "Change Password Successful!", Toast.LENGTH_SHORT).show()
            onChangePasswordSuccess()
        } else if(response is Resource.Error){
            errorMessage = response.message.toString()
            Log.d("Reset Password", response.message.toString())
            openErrorDialog =  true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                focusManager.clearFocus()
            }
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

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
                text = "Change Password",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(50.dp))

            PasswordTextField(
                content = oldPassword,
                onChangeValue = { oldPassword = it },
                placeholderContent = "Old Password",
                placeholderColor = MaterialTheme.colorScheme.onBackground,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next,
                onDone = { focusManager.moveFocus(FocusDirection.Down) },
                onNext = { focusManager.moveFocus(FocusDirection.Down)}
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTextField(
                content = newPassword,
                onChangeValue = { newPassword = it },
                placeholderContent = "New Password",
                placeholderColor = MaterialTheme.colorScheme.onBackground,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                onDone = { focusManager.moveFocus(FocusDirection.Down) },
                onNext = { focusManager.moveFocus(FocusDirection.Down)}
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTextField(
                content = confirmPassword,
                onChangeValue = { confirmPassword = it },
                placeholderContent = "Confirm New Password",
                placeholderColor = MaterialTheme.colorScheme.onBackground,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done,
                onDone = { focusManager.clearFocus() },
                onNext = { focusManager.clearFocus() }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (newPassword != confirmPassword) {
                        errorMessage = "Confirmation password does not match."
                        openErrorDialog = true
                    } else {
                        authenticationViewModel.changePassword(
                            ChangePassword(
                                oldPassword,
                                newPassword,
                                confirmPassword
                            ),
                            "Bearer ${accessToken.value}"
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
                    "Change Password",
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