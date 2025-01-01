package com.example.youmanage.viewmodel.taskmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.websocket.MemberObject
import com.example.youmanage.data.remote.websocket.WebSocketResponse
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.repository.WebSocketRepository
import com.example.youmanage.utils.Resource
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskManagementRepository: TaskManagementRepository,
    private val webSocketRepository: WebSocketRepository
): ViewModel() {

    private val _tasks = MutableLiveData<Resource<List<Task>>>()
    val tasks: LiveData<Resource<List<Task>>> = _tasks

    private val _taskSocket = MutableLiveData<Resource<WebSocketResponse<Task>>>()
    val taskSocket: LiveData<Resource<WebSocketResponse<Task>>> = _taskSocket

    fun getTasks(projectId: String, authorization: String) {
        viewModelScope.launch {

            val response = withContext(Dispatchers.IO){
                taskManagementRepository.getTasks(projectId, authorization)
            }

            _tasks.value = response
        }
    }

    fun connectToTaskWebSocket(url: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
               webSocketRepository.connectToSocket(
                   url,
                   object : TypeToken<WebSocketResponse<Task>>() {},
                   _taskSocket)
            }

        }

    }



}