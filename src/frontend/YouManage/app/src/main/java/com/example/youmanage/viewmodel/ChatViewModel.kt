package com.example.youmanage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.repository.ChatRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<Resource<List<MessageResponse>>>(Resource.Loading())
    val messages: StateFlow<Resource<List<MessageResponse>>> = _messages

    private val _sendStatus = MutableStateFlow<Resource<MessageResponse>>(Resource.Loading())
    val sendStatus: StateFlow<Resource<MessageResponse>> = _sendStatus

    // Lấy danh sách tin nhắn
    fun fetchMessages(roomId: Int) {
        viewModelScope.launch {
            _messages.value = Resource.Loading()
            _messages.value = repository.getMessages(roomId)
        }
    }

    // Gửi tin nhắn
    fun sendMessage(roomId: Int, message: MessageRequest) {
        viewModelScope.launch {
            _sendStatus.value = Resource.Loading()
            _sendStatus.value = repository.sendMessage(roomId, message)
        }
    }
}