package com.example.youmanage.screens.project_management

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.utils.Resource
import com.example.youmanage.viewmodel.AuthenticationViewModel

@Composable
fun UserProfileScreen(
    authenticationViewModel: AuthenticationViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {

    val accessToken by authenticationViewModel.accessToken.collectAsState(initial = null)
    val refreshToken by authenticationViewModel.refreshToken.collectAsState(initial = null)
    val logOutResponse by authenticationViewModel.logOutResponse.observeAsState()

    LaunchedEffect(logOutResponse) {
        if (logOutResponse is Resource.Success) {
            onLogout()
        }

        if (logOutResponse is Resource.Error) {
            Log.d("Logout", "Error: ${logOutResponse?.message}")
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            if(accessToken != null && refreshToken != null){
                authenticationViewModel.logOut(
                    logoutRequest = RefreshToken(refreshToken.toString()),
                    authorization = "Bearer $accessToken"
                )
            }
        }) {
            Text("Logout")
        }
    }
}