package com.example.youmanage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import com.example.youmanage.navigation.AuthenticationNavigation
import com.example.youmanage.ui.theme.YouManageTheme
import com.example.youmanage.utils.GoogleSignIn.googleSignIn
import com.example.youmanage.viewmodel.AuthenticationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouManageTheme {
                AuthenticationNavigation()
            }
        }
    }
}





