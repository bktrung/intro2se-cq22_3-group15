package com.example.youmanage.utils

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.utils.Constants.WEB_CLIENT_ID
import com.example.youmanage.viewmodel.AuthenticationViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object GoogleSignIn {
    fun googleSignIn(
        context: Context,
        coroutineScope: CoroutineScope,
        credentialManager: CredentialManager,
        viewModel: AuthenticationViewModel,
    ) {

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                handleSignIn(result, viewModel)
            } catch (e: Exception) {
                Log.d("Error", e.toString())
                redirectToGoogleLoginPage(context)
            }

        }
    }

    private fun redirectToGoogleLoginPage(context: Context) {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        }
        context.startActivity(intent)
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
        viewModel: AuthenticationViewModel
    ) {
        // Handle the successfully returned credential.
        when (val credential = result.credential) {
            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse on your server to
                // validate and authenticate
            }
            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        val googleIdToken = googleIdTokenCredential.idToken

                         viewModel.logInWithGoogle(UserGoogleLogIn(idToken = googleIdToken))

                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e("Error", "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e("Error", "Unexpected type of credential")
                }
            }
            else -> {
                // Catch any unrecognized credential type here.
                Log.e("Error", "Unexpected type of credential")
            }
        }
    }
}