package com.example.youmanage.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.websocket.MemberObject
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnackBarViewModel @Inject constructor(
    private val repository: WebSocketRepository
): ViewModel(){

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage

    private val _route = MutableStateFlow<String?>(null)
    val route: StateFlow<String?> = _route

    private val _changeRequestSocket = MutableLiveData<Resource<WebSocketResponse<ChangeRequest>>>()
    val changeRequestSocket: MutableLiveData<Resource<WebSocketResponse<ChangeRequest>>> = _changeRequestSocket

    /**
     * Gửi một thông báo đến snackbar
     * @param message Thông báo muốn hiển thị
     */
    fun showSnackBar(message: String, route: String? = null) {
        viewModelScope.launch {
            _snackBarMessage.emit(message)
            _route.emit(route)
        }
    }

    fun connectToChangeRequestWebsocket(url: String) {
        viewModelScope.launch {
            repository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<ChangeRequest>>() {},
                _changeRequestSocket
            )
        }
    }

}