package com.example.youmanage.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.factory.WebSocketFactory
import com.example.youmanage.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject

@ActivityScoped
class WebSocketRepository @Inject constructor(
    private val webSocketFactory: WebSocketFactory,
    private val gson: Gson
) {

    private var webSocket: WebSocket? = null
    private val _connectionStatus = MutableLiveData<ConnectionStatus>()
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus

    fun <T> connectToSocket(
        url: String,
        typeToken: TypeToken<WebSocketResponse<T>>,
        liveData: MutableLiveData<Resource<WebSocketResponse<T>>>
    ): Resource<WebSocketResponse<T>> {
        return try {
            webSocket = webSocketFactory.createWebSocket(
                url,
                object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        Log.d("WebSocket opened:", response.message)
                        _connectionStatus.postValue(ConnectionStatus.CONNECTED)
                        liveData.postValue(Resource.Success(WebSocketResponse()))
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        try {
                            val messageResponse: WebSocketResponse<T> = gson.fromJson(
                                text,
                                typeToken.type
                            )
                            Log.d("Received message:","$messageResponse")
                            liveData.postValue(Resource.Success(messageResponse))
                        } catch (e: JsonParseException) {
                            Log.e(e.toString(), "Error parsing WebSocket response")
                            liveData.postValue(
                                Resource.Error("Parsing error: ${e.localizedMessage}")
                            )
                        }
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        Log.e(t.toString(), "WebSocket failure")
                        _connectionStatus.postValue(ConnectionStatus.DISCONNECTED)
                        liveData.postValue(
                            Resource.Error("WebSocket failure: ${t.localizedMessage}")
                        )
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        Log.d("WebSocket closed:","$reason (Code: $code)")
                        _connectionStatus.postValue(ConnectionStatus.CLOSED)
                        liveData.postValue(
                            Resource.Error("WebSocket closed: $reason")
                        )
                    }
                }
            )
            Resource.Success(WebSocketResponse())
        } catch (e: Exception) {
            Log.e(e.toString(), "WebSocket connection error")
            _connectionStatus.postValue(ConnectionStatus.DISCONNECTED)
            Resource.Error("WebSocket error: ${e.localizedMessage}")
        }
    }

    fun closeConnection() {
        webSocket?.close(1000, "Closing connection")
        webSocket = null
        _connectionStatus.postValue(ConnectionStatus.CLOSED)
    }

    fun isConnected(): Boolean = _connectionStatus.value == ConnectionStatus.CONNECTED

    enum class ConnectionStatus {
        CONNECTED, DISCONNECTED, CLOSED
    }
}