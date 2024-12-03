package com.example.youmanage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.chat.MessageRequest
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.data.remote.chat.Messages
import com.example.youmanage.repository.ChatRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
): ViewModel() {

    private val _messages = MutableLiveData<Resource<Messages>>()
    val messages: MutableLiveData<Resource<Messages>> get() = _messages

    private val _message = MutableLiveData<Resource<MessageResponse>>()
    val message: MutableLiveData<Resource<MessageResponse>> get() = _message

    private val _response = MutableLiveData<Resource<String>>()
    val response: MutableLiveData<Resource<String>> get() = _response

    fun sendMessage(
        message: MessageRequest,
    ) {
        viewModelScope.launch {
            response.value = repository.sendMessage(messageRequest = message)
        }
    }

    fun connectToSocket(url: String) {
        viewModelScope.launch {
            repository.connectToSocket(url, _message)
        }
    }

    fun getMessage(
        projectId: String,
        authorization: String
    ){
        viewModelScope.launch {
            _messages.value = repository.getMessages(projectId = projectId, authorization = authorization)
        }
    }

}