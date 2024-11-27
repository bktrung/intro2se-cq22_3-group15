package com.example.youmanage.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.projectmanagement.Id
import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.projectmanagement.ProjectCreate
import com.example.youmanage.data.remote.projectmanagement.Projects
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.data.remote.taskmanagement.Username
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectManagementViewModel @Inject constructor(
    private val repository: ProjectManagementRepository
): ViewModel() {

    private val _projects = MutableLiveData<Resource<Projects>>()
    val projects: LiveData<Resource<Projects>> get() = _projects

    private val _project = MutableLiveData<Resource<Project>>()
    val project: LiveData<Resource<Project>> get() = _project

    private val _addMemberResponse = MutableLiveData<Resource<Detail>>()
    val addMemberResponse: LiveData<Resource<Detail>> get() = _addMemberResponse

    private val _deleteMemberResponse = MutableLiveData<Resource<Detail>>()
    val deleteMemberResponse: LiveData<Resource<Detail>> get() = _deleteMemberResponse

    private val _members = MutableLiveData<Resource<List<User>>>()
    val members: LiveData<Resource<List<User>>> get() = _members

    fun getProjectList(authorization: String) {
        viewModelScope.launch {
           _projects.value = repository.getProjectList(authorization = authorization)
        }
    }

    fun createProject(project: ProjectCreate, authorization: String) {
        viewModelScope.launch {
           repository.createProject(project = project, authorization = authorization)
        }
    }

    fun getProject(id: String, authorization: String) {
        viewModelScope.launch {
            _project.value = repository.getProject(id = id, authorization = authorization)
        }
    }

    fun updateFullProject(id: String, project: ProjectCreate, authorization: String) {
        viewModelScope.launch {
            repository.updateFullProject(id = id, project = project, authorization = authorization)
        }
    }

    fun updateProject(id: String, project: ProjectCreate, authorization: String) {
        viewModelScope.launch {
            repository.updateProject(id = id, project = project, authorization = authorization)
        }
    }

    fun deleteProject(id: String, authorization: String) {
        viewModelScope.launch {
            repository.deleteProject(id = id, authorization = authorization)
        }
    }

    fun addMember(id: String, username: Username, authorization: String) {
        viewModelScope.launch {
            _addMemberResponse.value = repository.addMember(
                id = id,
                member = username,
                authorization = authorization
            )
        }
    }

    fun removeMember(id: String, memberId: Id, authorization: String) {
        viewModelScope.launch {
            _deleteMemberResponse.value = repository.removeMember(
                id = id,
                memberId = memberId,
                authorization = authorization
            )
        }
    }

    fun getMembers(id: String, authorization: String){
        viewModelScope.launch {
            _members.value = repository.getMembers(id = id, authorization = authorization)
        }
    }
}

