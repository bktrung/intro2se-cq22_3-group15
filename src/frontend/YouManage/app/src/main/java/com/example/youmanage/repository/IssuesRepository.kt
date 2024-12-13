package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.issusemanagement.Issue
import com.example.youmanage.data.remote.issusemanagement.IssueCreate
import com.example.youmanage.data.remote.issusemanagement.IssueUpdate
import com.example.youmanage.utils.Resource
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IssuesRepository @Inject constructor(
    private val api: ApiInterface
) {

    suspend fun getIssues(projectId: String, authorization: String): Resource<List<Issue>> {
        return try {
            Resource.Success(api.getIssues(projectId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    suspend fun createIssue(
        projectId: String,
        issue: IssueCreate,
        authorization: String
    ): Resource<Issue> {
        return try {
            Resource.Success(api.createIssue(projectId, issue, authorization))
        } catch (e: HttpException) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    suspend fun getIssue(
        projectId: String,
        issueId: String,
        authorization: String
    ): Resource<Issue> {
        return try {
            Resource.Success(api.getIssue(projectId, issueId, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    suspend fun updateIssue(
        projectId: String,
        issueId: String,
        issueUpdate: IssueUpdate,
        authorization: String
    ): Resource<Issue> {
        return try {
            Resource.Success(api.updateIssue(projectId, issueId, issueUpdate, authorization))
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    suspend fun deleteIssue(
        projectId: String,
        issueId: String,
        authorization: String
    ): Resource<String> {
        return try {
            api.deleteIssue(projectId, issueId, authorization)
            Resource.Success("Issue has been deleted successfully.")
        } catch (e: HttpException) {
            Resource.Error("Issue not found")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }
}