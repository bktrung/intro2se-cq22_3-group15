package com.example.youmanage.viewmodel.projectmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.projectmanagement.UserId
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProjectMenuViewModel @Inject constructor(
    private val projectManagementRepository: ProjectManagementRepository
): ViewModel() {

    private val _deleteProjectResponse = MutableLiveData<Resource<String>>()
    val deleteProjectResponse: LiveData<Resource<String>> = _deleteProjectResponse

    private val _quitProjectResponse = MutableLiveData<Resource<Detail>>()
    val quitProjectResponse: LiveData<Resource<Detail>> = _quitProjectResponse

    private val _empowerResponse = MutableLiveData<Resource<Message>>()
    val empowerResponse: LiveData<Resource<Message>> = _empowerResponse

    private val _memberList = MutableLiveData<List<User>>(emptyList())
    val memberList: LiveData<List<User>> = _memberList

    private val _isHost = MutableLiveData<Boolean>(false)
    val isHost: LiveData<Boolean> = _isHost

    fun deleteProject(id: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.deleteProject(id, authorization)
            }
            _deleteProjectResponse.value = response
        }
    }

    fun quitProject(id: String, authorization: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.quitProject(id, authorization)
            }
            _quitProjectResponse.value = response
        }
    }

    fun getMemberList(id: String, authorization: String, hostId: Int) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.getProject(id, authorization)
            }
            if (response is Resource.Success) {
                _memberList.value = response.data?.members
                _memberList.value = _memberList.value?.filter { it.id != hostId }
            }
        }
    }

    fun empower(
        id: String,
        userId: Int,
        authorization: String
    ){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.empower(id, UserId(userId), authorization)
            }
            _empowerResponse.value = response
        }
    }

    fun isHost(id: String, authorization: String){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.isHost(id, authorization)
            }
            if (response is Resource.Success) {
                _isHost.value = response.data?.isHost ?: false
            }
        }
    }


}