package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class ChatRepository @Inject constructor(
    private val api: ApiInterface
) {
    // Gửi tin nhắn
    suspend fun sendMessage(roomId: Int, message: MessageRequest): Resource<MessageResponse> {
        return try {
            val response = api.sendMessage(roomId, message)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown Error")
        }
    }

    // Lấy danh sách tin nhắn
    suspend fun getMessages(roomId: Int): Resource<List<MessageResponse>> {
        return try {
            val response = api.getMessages(roomId)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown Error")
        }
    }
}
