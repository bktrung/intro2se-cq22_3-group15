package com.example.youmanage.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.Reply
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.repository.ChangeRequestRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeRequestViewModel @Inject constructor(
    private val repository: ChangeRequestRepository,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {

    private val _requests = MutableLiveData<Resource<List<ChangeRequest>>>()
    val requests: MutableLiveData<Resource<List<ChangeRequest>>> get() = _requests

    private val _response = MutableLiveData<Resource<ChangeRequest>>()
    val response: MutableLiveData<Resource<ChangeRequest>> get() = _response

    private val _reply = MutableLiveData<Resource<ChangeRequest>>()
    val reply: MutableLiveData<Resource<ChangeRequest>> get() = _reply

    fun getChangeRequest(
        projectId: Int,
        authorization: String
    ) {
        viewModelScope.launch {
            _requests.value = repository.getChangeRequests(projectId, authorization)
        }
    }

    fun createChangeRequest(
        projectId: Int,
        changeRequest: SendChangeRequest,
        authorization: String
    ) {
        viewModelScope.launch {
            repository.createChangeRequest(projectId, changeRequest, authorization)
        }
    }

    fun replyRequest(
        projectId: Int,
        requestId: Int,
        reply: Reply,
        authorization: String
    ) {
        viewModelScope.launch {
            _reply.value = repository.replyChangeRequest(projectId, reply, requestId, authorization)
        }

    }

    fun connectWebSocket(
        projectId: Int,
        authorization: String,
        view: View
    ){

    }

}