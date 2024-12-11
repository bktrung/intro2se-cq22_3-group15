package com.example.youmanage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.chat.MessageResponse
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.ChatRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import javax.inject.Inject


//@HiltViewModel
//class WebSocketViewModel @Inject constructor(
//    private val repository: WebSocketRepository
//) : ViewModel() {
//
//    private val _response = MutableLiveData<Resource<WebSocketResponse<*>>>() // * for wildcard generic
//    val response: LiveData<Resource<WebSocketResponse<*>>> get() = _response
//
//    fun <T> connectToWebsocket(url: String, type: Type) {
//        viewModelScope.launch {
//            repository.connectToSocket(url, type, _response as MutableLiveData<Resource<WebSocketResponse<T>>>)
//        }
//    }
//}
