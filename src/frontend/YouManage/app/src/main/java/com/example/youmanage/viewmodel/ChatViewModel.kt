package com.example.youmanage.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.chat.Message
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

    private val _messages = MutableLiveData<List<Message>>()
    val messages: MutableLiveData<List<Message>> get() = _messages

    private val _message = MutableLiveData<Resource<MessageResponse>>()
    val message: MutableLiveData<Resource<MessageResponse>> get() = _message

    private val _response = MutableLiveData<Resource<String>>()
    val response: MutableLiveData<Resource<String>> get() = _response

    private var nextCursor: String? = null
    private var preCursor: String? = null
    private var isLoading = false

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
        cursor: String? = null,
        authorization: String
    ){

        if(isLoading) return

        isLoading = true
        viewModelScope.launch {
            val response = repository.getMessages(projectId = projectId, cursor = cursor, authorization = authorization)
            try {
                if(response is Resource.Success){
                   response.data?.let {
                       nextCursor = it.next
                       preCursor = it.previous
                       _messages.value = (_messages.value ?: emptyList()) + it.results
                       Log.d("ChatViewModel", "Messages: ${_messages.value!!.size}")
                   }
                }
            } catch(e: Exception) {
                Log.e("ChatViewModel", "Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }


    fun getPreviousMessages(projectId: String, authorization: String){
        preCursor?.let {
            getMessage(projectId, it, authorization)
        }
    }

    fun getNextMessages(projectId: String, authorization: String){
        nextCursor?.let {
            getMessage(projectId, it, authorization)
        }
    }

}