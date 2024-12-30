package com.example.youmanage.repository

import android.util.Log
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.notification.DeviceTokenRequest
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val api: ApiInterface
)  {

     fun onNewToken(token: String) {
        Log.d("FirebaseService", "New Token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String) {
        runBlocking {
            try {
                val response = api.sendDeviceToken(DeviceTokenRequest(token))
                if (response.isSuccessful) {
                    Log.d("FirebaseService", "Token sent successfully")
                } else {
                    Log.e("FirebaseService", "Failed to send token")
                }
            } catch (e: Exception) {
                Log.e("FirebaseService", "Error sending token", e)
            }
        }
    }
}