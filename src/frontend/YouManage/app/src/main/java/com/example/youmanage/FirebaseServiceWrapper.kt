package com.example.youmanage

import android.util.Log
import com.example.youmanage.repository.FirebaseService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseServiceWrapper: FirebaseMessagingService() {

    @Inject
    lateinit var firebaseService: FirebaseService

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        firebaseService.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FirebaseServiceWrapper", "Message received: ${remoteMessage.data}")
    }
}