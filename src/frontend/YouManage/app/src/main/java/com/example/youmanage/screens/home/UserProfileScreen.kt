package com.example.youmanage.screens.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.utils.Constants.ACCESS_TOKEN_KEY
import com.example.youmanage.utils.Resource
import com.example.youmanage.utils.randomAvatar
import com.example.youmanage.viewmodel.auth.AuthenticationViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

@Composable
fun UserProfileScreen(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    onChangePassword:() -> Unit
) {
    val accessToken by authenticationViewModel.accessToken.collectAsState(initial = null)
    val refreshToken by authenticationViewModel.refreshToken.collectAsState(initial = null)
    val logOutResponse by authenticationViewModel.logOutResponse.observeAsState()
    val user by authenticationViewModel.user.observeAsState()
    val isGoogleLogIn by authenticationViewModel.getGoogleLogin().collectAsState()

    var openLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(logOutResponse) {
        if (logOutResponse is Resource.Success) {
            onLogout()
            Log.d("Logout", "Success: ${logOutResponse?.data}")
        }
        else if (logOutResponse is Resource.Error) {
            Log.d("Logout", "Error: ${logOutResponse?.message}")
        }
    }

    LaunchedEffect(accessToken) {
        accessToken?.let { token ->
            supervisorScope {
               val job = launch {
                    authenticationViewModel.getUser(authorization = "Bearer $token")
                }

                job.join()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), // Background màu nền
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(id = randomAvatar(user?.data?.id ?: 0)),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Welcome Text
            Text(
                text = "Welcome, ${user?.data?.username}",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email Text
            Text(
                text = "Email: ${user?.data?.email}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Change Password Button
            if (!isGoogleLogIn) {
                Button(
                    onClick = onChangePassword,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Change Password",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logout Button
            Button(
                onClick = { openLogoutDialog = true },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text(
                    text = "Logout",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Loading Indicator
            AnimatedVisibility(
                visible = logOutResponse is Resource.Loading,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                if (logOutResponse is Resource.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    AlertDialog(
        title = "Log out ?",
        content = "Are you sure you want to log out?",
        showDialog = openLogoutDialog,
        onDismiss = {
            openLogoutDialog = false
        },
        onConfirm = {
            if (accessToken != null && refreshToken != null) {
                authenticationViewModel.logOut(
                    logoutRequest = RefreshToken(refreshToken!!),
                    authorization = "Bearer $accessToken"
                )
            }
            authenticationViewModel.clearToken(ACCESS_TOKEN_KEY)
            openLogoutDialog = false
        })
}

