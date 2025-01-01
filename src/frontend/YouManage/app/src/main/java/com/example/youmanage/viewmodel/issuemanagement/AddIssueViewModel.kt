package com.example.youmanage.viewmodel.issuemanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.data.remote.issusemanagement.IssueCreate
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Task
import com.example.youmanage.data.remote.taskmanagement.TaskCreate
import com.example.youmanage.repository.IssuesRepository
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.repository.TaskManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddIssueViewModel @Inject constructor(
    private val projectManagementRepository: ProjectManagementRepository,
    private val taskManagementRepository: TaskManagementRepository,
    private val issueManagementRepository: IssuesRepository
): ViewModel() {

    private val _members = MutableLiveData<List<User>>(emptyList())
    val members: LiveData<List<User>> = _members

    private val _tasks = MutableLiveData<List<Task>>(emptyList())
    val tasks: LiveData<List<Task>> = _tasks

    private val _issue = MutableLiveData<Resource<Issue>>()
    val issue: LiveData<Resource<Issue>> = _issue

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

    fun getTasks(projectId: String, authorization: String){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                taskManagementRepository.getTasks(projectId, authorization)
            }

            if(response is Resource.Success){
                _tasks.value = response.data ?: emptyList()
            }
        }
    }

    fun loadIssueData(projectId: String, authorization: String) {
        viewModelScope.launch {
            supervisorScope {
                val getMembersJob = launch { getMembers(projectId, authorization) }
                val getTasksJob = launch { getTasks(projectId, authorization) }

                // Chờ cả hai hoàn thành nếu cần
                getMembersJob.join()
                getTasksJob.join()
            }
        }
    }

    fun createIssue(
        projectId: String,
        issue: IssueCreate,
        authorization: String
    ) {
        viewModelScope.launch {
            _issue.value = withContext(Dispatchers.IO){
                issueManagementRepository.createIssue(projectId, issue, authorization)
            }
        }
    }

}