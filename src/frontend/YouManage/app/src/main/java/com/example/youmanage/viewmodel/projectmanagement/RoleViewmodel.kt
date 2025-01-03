package com.example.youmanage.viewmodel.projectmanagement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.Role
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.data.remote.taskmanagement.Detail
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoleViewmodel @Inject constructor(
    private val repository: ProjectManagementRepository
) : ViewModel() {

    private val supervisorJob = SupervisorJob() // Tạo SupervisorJob
    private val scope = CoroutineScope(Dispatchers.Main + supervisorJob) // Tạo CoroutineScope với SupervisorJob

    private val _roles: MutableLiveData<Resource<List<Role>>> = MutableLiveData()
    val roles: MutableLiveData<Resource<List<Role>>> get() = _roles

    private val _role: MutableLiveData<Resource<Role>> = MutableLiveData()
    val role: MutableLiveData<Resource<Role>> get() = _role

    private val _response = MutableLiveData<Resource<Role>>()
    val response: MutableLiveData<Resource<Role>> get() = _response

    private val _deleteResponse = MutableLiveData<Resource<String>>()
    val deleteResponse: MutableLiveData<Resource<String>> get() = _deleteResponse

    private val _assignResponse = MutableLiveData<Resource<Detail>>()
    val assignResponse: MutableLiveData<Resource<Detail>> get() = _assignResponse

    private val _members = MutableLiveData<List<Map<User, Boolean>>>()
    val members: MutableLiveData<List<Map<User, Boolean>>> get() = _members

    // Cập nhật các hàm sử dụng scope mới

    fun getRoles(
        projectId: String,
        authorization: String
    ) {
        scope.launch {
            _roles.value = repository.getRoles(
                projectId,
                authorization
            )
        }
    }

    fun getRole(
        projectId: String,
        roleId: String,
        authorization: String
    ) {
        scope.launch {
            _role.value = repository.getRole(
                projectId,
                roleId,
                authorization
            )
        }
    }

    fun createRole(
        projectId: String,
        role: RoleRequest,
        authorization: String
    ) {
        scope.launch {
            _response.value = repository.createRole(
                projectId,
                role,
                authorization
            )
        }
    }

    fun updateRole(
        projectId: String,
        roleId: String,
        role: RoleRequest,
        authorization: String
    ) {
        scope.launch {
            _response.value = repository.updateRole(
                projectId,
                roleId,
                role,
                authorization
            )
        }
    }

    fun deleteRole(
        projectId: String,
        roleId: String,
        authorization: String
    ) {
        scope.launch {
            _deleteResponse.value = repository.deleteRole(
                projectId,
                roleId,
                authorization
            )
        }
    }

    fun assignRole(
        projectId: String,
        roleId: String,
        member: Assign,
        action: String,
        authorization: String
    ) {
        scope.launch {
            _assignResponse.value = repository.assignRole(
                projectId,
                roleId,
                action,
                member,
                authorization
            )
        }
    }

    fun getListMemberOfRole(
        projectId: String,
        roleId: String,
        authorization: String
    ) {
        scope.launch {
            val member = repository.getMembers(
                projectId,
                authorization
            )

            val unAssignedMembers = repository.getNonMember(
                projectId,
                roleId,
                authorization
            )

            _members.value = member.data?.map {
                mapOf(it to (unAssignedMembers.data?.contains(it) ?: false))
            }
        }
    }

    fun getRolesOfMember(
        projectId: String,
        memberId: String,
        authorization: String
    ) {
        scope.launch {
            _roles.value = repository.getRolesOfMember(
                projectId,
                memberId,
                authorization
            )
        }
    }


}
