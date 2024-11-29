package com.example.youmanage.screens.project_management

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.screens.components.AlertDialog
import com.example.youmanage.utils.Constants.ACCESS_TOKEN_KEY
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel
import kotlin.math.log

@Composable
fun UserProfileScreen(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val accessToken by authenticationViewModel.accessToken.collectAsState(initial = null)
    val refreshToken by authenticationViewModel.refreshToken.collectAsState(initial = null)
    val logOutResponse by authenticationViewModel.logOutResponse.observeAsState()
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

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            openLogoutDialog = true
        }) {
            Text("Logout")
        }
        
        AnimatedVisibility(
            visible = logOutResponse is Resource.Loading,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            if(logOutResponse is Resource.Loading){
                CircularProgressIndicator()
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
            Log.d("Logout", "Access Token: $accessToken")
            Log.d("Logout", "Refresh Token: $refreshToken")
            if (accessToken != null && refreshToken != null) {
                Log.d("Logout", "Access Token: $accessToken")
                Log.d("Logout", "Refresh Token: $refreshToken")
                authenticationViewModel.logOut(
                    logoutRequest = RefreshToken(refreshToken!!),
                    authorization = "Bearer $accessToken"
                )
            }
            authenticationViewModel.clearToken(ACCESS_TOKEN_KEY)
            openLogoutDialog = false
        })
}

