package com.example.youmanage.viewmodel.projectmanagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.projectmanagement.Assign
import com.example.youmanage.data.remote.projectmanagement.Role
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.repository.ProjectManagementRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MemberProfileViewModel @Inject constructor(
    private val projectManagementRepository: ProjectManagementRepository
): ViewModel() {

    private val _member = MutableStateFlow(User())
    val member: StateFlow<User> = _member

    private val _roles = MutableStateFlow<List<Role>>(emptyList())
    val roles: StateFlow<List<Role>> = _roles

    private val _unAssignResponse = MutableStateFlow(false)
    val unAssignResponse: StateFlow<Boolean> = _unAssignResponse

    private fun getMember(projectId: String, memberId: String, token: String){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.getProject(projectId, "Bearer $token")
            }

            if (response is Resource.Success) {
               val members = response.data?.members ?: emptyList()
                _member.value = members.find { it.id == memberId.toInt() } ?: User()
            }
        }
    }

    fun getRoles(projectId: String, memberId: String, token: String){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.getRolesOfMember(
                    projectId,
                    memberId,
                    "Bearer $token")
            }

            if(response is Resource.Success){
                _roles.value = response.data ?: emptyList()
            }
        }
    }

    fun getMemberAndRoles(projectId: String, memberId: String, token: String) {
        viewModelScope.launch {
            // Sử dụng supervisorScope để chạy song song các coroutine
            supervisorScope {
                // Chạy song song hai hàm
                val getMemberJob = launch {
                    getMember(projectId, memberId, token)
                }
                val getRolesJob = launch {
                    getRoles(projectId, memberId, token)
                }

                // Đợi cả hai coroutine hoàn thành
                getMemberJob.join()
                getRolesJob.join()
            }
        }
    }

    fun unAssignRole(projectId: String,roleId: String, memberId: Int, token: String) {
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO) {
                projectManagementRepository.assignRole(
                    projectId= projectId,
                    roleId = roleId,
                    action = "unassign",
                    member = Assign(memberId),
                    authorization = "Bearer $token"
                )
            }

            if(response is Resource.Success){
                _unAssignResponse.value = true
            }
        }
    }

}