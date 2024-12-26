package com.example.youmanage.screens.authetication

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
import com.example.youmanage.data.remote.authentication.Email
import com.example.youmanage.screens.components.ErrorDialog
import com.example.youmanage.screens.components.TextFieldComponent
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel

@Composable
fun FindUserScreen(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onFindSuccess: (String) -> Unit
) {

    var openErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val response = authenticationViewModel.message.observeAsState().value
    var user by remember { mutableStateOf("") }

    LaunchedEffect(response) {
        if (response is Resource.Success) {
            authenticationViewModel.forgotPasswordSendOTP(Email(user))
            onFindSuccess(user)
        } else if (response is Resource.Error) {
            errorMessage = response.message.toString()
            openErrorDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(vertical = 100.dp)

        ) {


            Image(
                painter = painterResource(id = R.drawable.find_user_img),
                contentDescription = "Reset Password",
                modifier = Modifier.size(200.dp)

            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Find your account",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Enter your email",
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(40.dp))

            TextFieldComponent(
                content = user,
                onChangeValue = { user = it },
                placeholderContent = "Email",
                placeholderColor = Color.Gray,
                containerColor = MaterialTheme.colorScheme.surface,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    authenticationViewModel.checkEmail(Email(user))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
            )
            {
                Text(
                    "Continue",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 5.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    if (openErrorDialog) {
        ErrorDialog(title = "Error",
            content = errorMessage,
            showDialog = true,
            onDismiss = { openErrorDialog = false },
            onConfirm = { openErrorDialog = false })
    }

}