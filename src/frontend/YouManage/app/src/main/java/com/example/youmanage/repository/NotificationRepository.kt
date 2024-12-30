package com.example.youmanage.repository

import androidx.lifecycle.MutableLiveData
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.data.remote.notification.Count
import com.example.youmanage.data.remote.notification.NotificationSocket
import com.example.youmanage.data.remote.notification.Notifications
import com.example.youmanage.factory.WebSocketFactory
import com.example.youmanage.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

@ActivityScoped
class NotificationRepository @Inject constructor(
    private val webSocketFactory: WebSocketFactory,
    private val api: ApiInterface
) {

    private var webSocket: WebSocket? = null

    fun connectToSocket(
        url: String, liveData: MutableLiveData<Resource<NotificationSocket>>
    ): Resource<MessageResponse> {
        return try {
            webSocket = webSocketFactory.createWebSocket(url, object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    println("Notification WebSocket opened: ${response.message}")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    try {
                        val messageResponse = Gson().fromJson(text, NotificationSocket::class.java)
                        println("Received notification: $messageResponse")
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
                    println("Notification WebSocket failure: ${t.message}")
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    println("Notification WebSocket closed: $reason")
                }
            })
            Resource.Success(MessageResponse(0, "Connected", "", "",""))
        } catch (e: Exception) {
            Resource.Error("Error with Notification WebSocket: ${e.localizedMessage}")
        }
    }

    suspend fun getNotifications(
        cursor: String? = null,
        authorization: String
    ): Resource<Notifications> = safeApiCall {
        api.getNotifications(authorization = authorization, cursor = cursor)
    }


    suspend fun getUnreadCountNotifications(authorization: String) : Resource<Count>
    = safeApiCall {
        api.getUnreadCountNotifications(authorization)
    }

    suspend fun markAsRead(
        notificationId: Int,
        authorization: String
    ) : Resource<Message> = safeApiCall {
        api.markAsRead(notificationId, authorization)
    }


    suspend fun readAll(
        authorization: String
    ):Resource<Message> = safeApiCall {
        api.readAll(authorization)
    }

    suspend fun deleteNotification(
        notificationId: Int,
        authorization: String
    ): Resource<String> {
        return try {
            api.deleteNotification(notificationId, authorization)
            Resource.Success("Delete notification successfully !")
        } catch (e: retrofit2.HttpException){
            Resource.Error(e.message.toString())
        } catch (e: Exception){
            Resource.Error(e.message.toString())
        }
    }
}