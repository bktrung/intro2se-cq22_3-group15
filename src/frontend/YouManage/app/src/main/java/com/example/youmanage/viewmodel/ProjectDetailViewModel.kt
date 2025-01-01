package com.example.youmanage.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.data.remote.websocket.MemberObject
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.screens.components.PieChartInput
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val projectManagementRepository: ProjectManagementRepository,
    private val webSocketRepository: WebSocketRepository
): ViewModel(){

    private val _progress = MutableLiveData<List<PieChartInput>>(emptyList())
    val progress: LiveData<List<PieChartInput>> = _progress

    private val _project = MutableLiveData<Resource<Project>>()
    val project: LiveData<Resource<Project>> = _project

    private val _isHost = MutableLiveData<Boolean>(false)
    val isHost: LiveData<Boolean> = _isHost

    private val _addMemberResponse = MutableLiveData<Resource<Detail>>()
    val addMemberResponse: LiveData<Resource<Detail>> = _addMemberResponse

    private val _removeMemberResponse = MutableLiveData<Resource<Detail>>()
    val removeMemberResponse: LiveData<Resource<Detail>> = _removeMemberResponse

    private val _projectSocket = MutableLiveData<Resource<WebSocketResponse<Project>>>()
    val projectSocket: LiveData<Resource<WebSocketResponse<Project>>> = _projectSocket

    private val _memberSocket = MutableLiveData<Resource<WebSocketResponse<MemberObject>>>()
    val memberSocket: LiveData<Resource<WebSocketResponse<MemberObject>>> = _memberSocket

    private val _taskSocket = MutableLiveData<Resource<WebSocketResponse<Task>>>()
    val taskSocket: LiveData<Resource<WebSocketResponse<Task>>> = _taskSocket

    fun addMember(projectId: String, username: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.addMember(
                    projectId,
                    Username(username),
                    authorization)
            }

            if(response is Resource.Success){
                _addMemberResponse.value = response
            }
        }
    }

    fun removeMember(projectId: String, memberId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.removeMember(
                    projectId,
                    Id(memberId),
                    authorization
                )
            }

            if (response is Resource.Success) {
                _removeMemberResponse.value = response
            }
        }
    }
    fun isHost(id: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.isHost(id, authorization)
            }
            if (response is Resource.Success) {
                _isHost.value = true
            }
        }
    }

    fun getProject(projectId: String, authorization: String){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                projectManagementRepository.getProject(projectId, authorization)
            }
            if(response is Resource.Success){
                _project.value = response
            }
        }
    }

    fun getProgressTrack(projectId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                projectManagementRepository.getProgressTrack(projectId, authorization)
            }
            if(response is Resource.Success){
                val data = response.data
                var total = data?.total ?: 1
                var pending = data?.pending ?: 0
                val inProgress = data?.inProgress ?: 0
                val completed = data?.completed ?: 0

                if (total == 0) {
                    total = 1
                    pending = 1
                }

                _progress.value = listOf(
                    PieChartInput(
                        color = Color(0xffFFD580),
                        value = pending.toDouble().div(total) * 100.0,
                        description = "Pending"
                    ),
                    PieChartInput(
                        color = Color(0xff90CAF9),
                        value = inProgress.toDouble().div(total) * 100.0,
                        description = "In Progress"
                    ),
                    PieChartInput(
                        color = Color(0xffA5D6A7),
                        value = completed.toDouble().div(total) * 100.0,
                        description = "Completed"
                    )
                )
            }
        }
    }

    fun connectToProjectWebsocket(url: String) {
        viewModelScope.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<Project>>() {},
                _projectSocket
            )
        }
    }

    fun connectToMemberWebsocket(url: String) {
        viewModelScope.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<MemberObject>>() {},
                _memberSocket
            )
        }
    }

    fun connectToTaskWebSocket(url: String){
        viewModelScope.launch {
            webSocketRepository.connectToSocket(
                url,
                object : TypeToken<WebSocketResponse<Task>>() {},
                _taskSocket
            )
        }
    }

    fun connectToAllWebSockets(url: String) {
        viewModelScope.launch {
            supervisorScope {
                // Chạy song song 3 websocket
                launch {
                    connectToProjectWebsocket(url)
                }
                launch {
                    connectToMemberWebsocket(url)
                }
                launch {
                    connectToTaskWebSocket(url)
                }
            }
        }
    }



}