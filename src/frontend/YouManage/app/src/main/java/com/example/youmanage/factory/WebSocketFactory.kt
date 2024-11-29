package com.example.youmanage.factory

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

class WebSocketFactory @Inject constructor(private val okHttpClient: OkHttpClient) {
    fun createWebSocket(url: String, listener: WebSocketListener): WebSocket {
        val request = Request.Builder()
            .url(url)
            .build()
        return okHttpClient.newWebSocket(request, listener)
    }
}