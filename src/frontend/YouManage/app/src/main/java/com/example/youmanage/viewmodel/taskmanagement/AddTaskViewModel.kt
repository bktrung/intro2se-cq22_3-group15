package com.example.youmanage.viewmodel.taskmanagement

import android.view.View
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.repository.ChangeRequestRepository
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddTaskViewModel @Inject constructor(
    private val taskManagementRepository: TaskManagementRepository,
    private val projectManagementRepository: ProjectManagementRepository,
    private val changeRequestRepository: ChangeRequestRepository
): ViewModel() {

    private val _members = MutableLiveData<List<User>>(emptyList())
    val members: LiveData<List<User>> = _members

    private val _createResponse = MutableLiveData<Resource<Task>>()
    val createResponse: LiveData<Resource<Task>> = _createResponse

    private val _requestResponse = MutableLiveData<Resource<ChangeRequest>>()
    val requestResponse: LiveData<Resource<ChangeRequest>> = _requestResponse

    private val _isHost = MutableLiveData<Boolean>()
    val isHost: LiveData<Boolean> = _isHost

    fun isHost(projectId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.isHost(projectId, authorization)
            }

            if (response is Resource.Success) {
                _isHost.value = response.data?.isHost ?: false
            }
        }
    }

    fun getMembers(projectId: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                projectManagementRepository.getMembers(projectId, authorization)
            }

            if(response is Resource.Success){
                _members.value = response.data ?: emptyList()
            }
        }
    }

    fun createTask(
        projectId: String,
        task: TaskCreate,
        authorization: String
    ){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                taskManagementRepository.createTask(projectId, task, authorization)
            }

            _createResponse.value = response
        }
    }

    fun sendCreateRequest(
        projectId: Int,
        task: TaskCreate,
        description: String,
        authorization: String
    ){
        viewModelScope.launch {
           val response = withContext(Dispatchers.IO) {
               changeRequestRepository.createChangeRequest(
                   projectId,
                   SendChangeRequest(
                       requestType = "CREATE",
                       targetTable = "TASK",
                       targetTableId = null,
                       description = description,
                       newData = task
                   ),
                   authorization
               )
           }
            _requestResponse.value = response
        }
    }


}