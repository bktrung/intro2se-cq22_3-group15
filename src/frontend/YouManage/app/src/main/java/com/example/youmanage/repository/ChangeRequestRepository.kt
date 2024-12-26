package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.changerequest.ChangeRequest
import com.example.youmanage.data.remote.changerequest.Reply
import com.example.youmanage.data.remote.changerequest.SendChangeRequest
import com.example.youmanage.data.remote.projectmanagement.RoleRequest
import com.example.youmanage.data.remote.taskmanagement.TaskUpdate
import com.example.youmanage.utils.Resource
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.HttpException
import javax.inject.Inject


@ActivityScoped
class ChangeRequestRepository @Inject constructor(
    private val api: ApiInterface
) {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(apiCall())
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    private fun <T> handleHttpException(e: HttpException): Resource.Error<T> {
        return when (e.code()) {
            400 -> {
                val errorBody = e.response()?.errorBody()?.string()
                Resource.Error(errorBody.toString())
            }

            404 -> Resource.Error("User not found")
            else -> Resource.Error("HTTP Error: ${e.code()}")
        }
    }

    suspend fun getChangeRequests(
        projectId: Int,
        authorization: String
    ): Resource<List<ChangeRequest>> = safeApiCall { api.getRequest(projectId, authorization) }

    suspend fun createChangeRequest(
        projectId: Int,
        changeRequest: SendChangeRequest,
        authorization: String
    ): Resource<ChangeRequest> = safeApiCall {
        // Gọi API và nhận Response
        api.createRequest(projectId, changeRequest, authorization)

    }

    suspend fun replyChangeRequest(
        projectId: Int,
        reply: Reply,
        changeRequestId: Int,
        authorization: String
    ): Resource<ChangeRequest> = safeApiCall {
        api.replyRequest(
            projectId,
            changeRequestId,
            reply,
            authorization
        )
    }
}