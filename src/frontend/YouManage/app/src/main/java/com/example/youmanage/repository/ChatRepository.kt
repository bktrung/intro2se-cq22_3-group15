package com.example.youmanage.repository

import androidx.lifecycle.MutableLiveData
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.data.remote.chat.Messages
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskWebSocket
import com.example.youmanage.factory.WebSocketFactory
import com.example.youmanage.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

@ActivityScoped
class ChatRepository @Inject constructor(
    private val api: ApiInterface,
    private val webSocketFactory: WebSocketFactory
) {

    private var webSocket: WebSocket? = null

    fun connectToSocket(
        url: String, liveData: MutableLiveData<Resource<MessageResponse>>): Resource<MessageResponse> {
        return try {
            webSocket = webSocketFactory.createWebSocket(url, object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    println("WebSocket opened: ${response.message}")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    try {
                        val messageResponse = Gson().fromJson(text, MessageResponse::class.java)
                        println("Received message: $messageResponse")
                        liveData.postValue(Resource.Success(messageResponse))
                        Resource.Success(messageResponse)
                    } catch (e: Exception) {
                        println("Error parsing response: ${e.message}")
                        liveData.postValue(Resource.Error("Error parsing response: ${e.message}"))
                        Resource.Error("Error parsing response: ${e.message}")
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                    println("WebSocket failure: ${t.message}")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    println("WebSocket closed: $reason")
                }
            })
            Resource.Success(MessageResponse(0, "Connected", "", "",""))
        } catch (e: Exception) {
            Resource.Error("Error with WebSocket: ${e.localizedMessage}")
        }
    }

    fun sendMessage(messageRequest: MessageRequest): Resource<String> {
        return try {
            val jsonMessage = Gson().toJson(messageRequest)
            webSocket?.send(jsonMessage) ?: return Resource.Error("WebSocket is not connected")
            Resource.Success("Message sent: $jsonMessage")
        } catch (e: Exception) {
            Resource.Error("Error sending message: ${e.localizedMessage}")
        }
    }

    suspend fun getMessages(
        projectId: String,
        cursor: String? = null,
        authorization: String): Resource<Messages> {
        return try {
            val response = api.getMessage(projectId = projectId, cursor = cursor, authorization = authorization)
            Resource.Success(response)
        } catch (e: Exception) {
            Resource.Error("Error getting messages: ${e.localizedMessage}")
        }
    }

}