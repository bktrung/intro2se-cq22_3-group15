package com.example.youmanage.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.chat.Message
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.repository.ChatRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val supervisorJob = SupervisorJob() // Tạo SupervisorJob
    private val scope = CoroutineScope(Dispatchers.Main + supervisorJob) // Tạo CoroutineScope với SupervisorJob

    private val _messages = MutableLiveData<List<Message>>()
    val messages: MutableLiveData<List<Message>> get() = _messages

    private val _messageSocket = MutableLiveData<Resource<MessageResponse>>()
    val messageSocket: MutableLiveData<Resource<MessageResponse>> get() = _messageSocket

    private val _response = MutableLiveData<Resource<String>>()
    val response: MutableLiveData<Resource<String>> get() = _response

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> get() = _isLoading

    private var nextCursor: String? = null
    private var preCursor: String? = null

    fun sendMessage(
        message: MessageRequest,
    ) {
        scope.launch {
            response.value = repository.sendMessage(messageRequest = message)
        }
    }

    fun connectToSocket(url: String) {
        scope.launch {
            repository.connectToSocket(url, _messageSocket)
        }
    }

    fun getNewSocketMessage(
        projectId: String,
        authorization: String
    ) {
        scope.launch {
            // Lấy các tin nhắn mới từ API
            val newListMessage = repository.getMessages(
                projectId = projectId,
                authorization = authorization
            ).data?.results ?: emptyList()

            // Cập nhật danh sách tin nhắn
            _messages.value = when {
                _messages.value.isNullOrEmpty() -> newListMessage // Nếu danh sách rỗng, chỉ cần gán mới
                _messages.value!!.size <= 19 -> newListMessage // Nếu danh sách có 19 phần tử hoặc ít hơn, thay thế hoàn toàn
                else -> newListMessage + _messages.value!!.drop(19) // Nếu có hơn 19 phần tử, bỏ qua 19 phần tử đầu và thêm mới vào đầu
            }
        }
    }

    fun getMessages(
        projectId: String,
        cursor: String? = null,
        authorization: String
    ) {
        scope.launch {
            _isLoading.value = true

            val response = repository.getMessages(
                projectId = projectId,
                cursor = cursor,
                authorization = authorization
            )

            try {
                if (response is Resource.Success) {
                    response.data?.let {
                        nextCursor = it.next?.substringAfter("cursor=")
                        Log.d("ChatViewModel", "getMessages: $nextCursor")
                        preCursor = it.previous
                        _messages.value = (_messages.value ?: emptyList()) + it.results
                        Log.d("ChatViewModel", "getMessages: ${_messages.value}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Exception: ${e.message}")
            } finally {
                delay(500)
                isLoading.value = false
            }
        }
    }

    fun getPreviousMessages(projectId: String, authorization: String) {
        preCursor?.let {
            getMessages(projectId, it, authorization)
        }
    }

    fun getNextMessages(projectId: String, authorization: String) {
        nextCursor?.let {
            getMessages(projectId, it, authorization)
        }
    }

}
