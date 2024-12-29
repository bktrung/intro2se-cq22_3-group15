package com.example.youmanage.viewmodel

import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

val requestStatus = listOf(
    "Pending" to "PENDING",
    "Approved" to "APPROVED",
    "Rejected" to "REJECTED"
)

@HiltViewModel
class ChangeRequestViewModel @Inject constructor(
    private val repository: ChangeRequestRepository,
    private val webSocketRepository: WebSocketRepository
) : ViewModel() {

    private val _requests = MutableLiveData<List<ChangeRequest>>(emptyList())
    val requests: MutableLiveData<List<ChangeRequest>> get() = _requests

    private val _response = MutableLiveData<Resource<ChangeRequest>>()
    val response: MutableLiveData<Resource<ChangeRequest>> get() = _response

    private val _reply = MutableLiveData<Resource<ChangeRequest>>()
    val reply: MutableLiveData<Resource<ChangeRequest>> get() = _reply

    var currentStatus = MutableStateFlow("PENDING")

    private var nextCursor: String? = null
    private var preCursor: String? = null
    var isLoading = MutableStateFlow(false)

    fun getChangeRequests(
        projectId: Int,
        cursor: String? = null,
        status: String? = null,
        authorization: String,
        isChangeStatus: Boolean = false
    ) {
        viewModelScope.launch {

            isLoading.value = true
            currentStatus.value = status ?: "PENDING"

            val response = repository.getChangeRequests(
                projectId,
                cursor,
                status,
                authorization
            )

            try {
                if(response is Resource.Success){
                    response.data?.let {

                        if(nextCursor == null){
                            _requests.value = emptyList()
                        }

                        nextCursor = it.next?.substringAfter("cursor=")?.substringBefore("&")

                        Log.d("ChangeRequestViewModel", "Change Request Cursor: $nextCursor")

                        _requests.value = _requests.value?.plus(it.results)

                        Log.d("ChangeRequestViewModel", "Change Requests: ${_requests.value?.size}")
                    }
                }
            } catch(e: Exception) {
                Log.e("ChatViewModel", "Exception: ${e.message}")
            } finally {
                delay(500)
                isLoading.value = false
            }
        }
    }

    fun loadMore(
        projectId: Int,
        status: String? = null,
        authorization: String
    ){
        nextCursor?.let {
            getChangeRequests(projectId, it, status, authorization)
        }
    }

    fun createChangeRequest(
        projectId: Int,
        changeRequest: SendChangeRequest,
        authorization: String
    ) {
        viewModelScope.launch {
            repository.createChangeRequest(
                projectId,
                changeRequest,
                authorization)
        }
    }

    fun replyRequest(
        projectId: Int,
        requestId: Int,
        reply: Reply,
        authorization: String
    ) {
        viewModelScope.launch {
            val response = repository.replyChangeRequest(projectId, reply, requestId, authorization)
            if(_reply.value is Resource.Success){
                _requests.value = _requests.value?.filter { it.id != requestId }
            }
        }
    }

    fun connectWebSocket(
        projectId: Int,
        authorization: String,
        view: View
    ){

    }

}