package com.example.youmanage.repository

import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.utils.Resource
import dagger.hilt.android.scopes.ActivityScoped
import retrofit2.HttpException
import javax.inject.Inject

@ActivityScoped
class AuthenticationRepository @Inject constructor(
    private val api: ApiInterface
) {

    suspend fun signUp(user: UserSignUp): Resource<UserSignUpResponse> {
        val response = try {
            Resource.Success(api.signUp(user))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error("${e.response()?.errorBody()?.string()}")
            } else {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun logIn(user: UserLogIn): Resource<UserLogInResponse> {
        val response = try {
            Resource.Success(api.logIn(user))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error("${e.response()?.errorBody()?.string()}")
            }
            else if (e.code() == 401) {
                Resource.Error("${e.response()?.errorBody()?.string()}")
            }
            else {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun logInWithGoogle(user: UserGoogleLogIn): Resource<UserLogInResponse> {

        val response = try {
            Resource.Success(api.logInWithGoogle(user))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error("${e.response()?.errorBody()?.string()}")
            } else {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
        return response
    }

}