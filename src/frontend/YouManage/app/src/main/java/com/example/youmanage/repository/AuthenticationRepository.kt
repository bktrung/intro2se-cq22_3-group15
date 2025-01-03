package com.example.youmanage.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.authentication.AccessToken
import com.example.youmanage.data.remote.authentication.ChangePassword
import com.example.youmanage.data.remote.authentication.ResetPassword
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.authentication.Email
import com.example.youmanage.data.remote.authentication.ResetToken
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.data.remote.notification.DeviceTokenRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.utils.Constants.ACCESS_TOKEN_KEY
import com.example.youmanage.utils.Constants.REFRESH_TOKEN_KEY
import com.example.youmanage.utils.Resource
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessToken")

@ActivityScoped
class AuthenticationRepository @Inject constructor(
    private val api: ApiInterface,
    @ApplicationContext private val context: Context
) {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(apiCall())
        } catch (e: HttpException) {
            Log.d("Error", e.message().toString())
            handleHttpException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
    }

    private fun <T> handleHttpException(e: HttpException): Resource.Error<T> {
        return when (e.code()) {
            400 -> {
                Resource.Error("${e.response()?.errorBody()?.string()}")
            }
            401 -> {
                Resource.Error("Unauthorized - ${e.response()?.errorBody()?.string()}")
            }
            404 -> {
                Resource.Error("Not Found - ${e.response()?.errorBody()?.string()}")
            }
            else -> {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        }
    }

    suspend fun signUp(user: UserSignUp): Resource<UserSignUpResponse> {
        return safeApiCall { api.signUp(user) }
    }

    suspend fun logIn(user: UserLogIn): Resource<UserLogInResponse> {
        val loginResponse = safeApiCall { api.logIn(user) }
        if (loginResponse is Resource.Success) {
            val token = FirebaseMessaging.getInstance().token.await()
            safeApiCall { api.assignDeviceToken(DeviceTokenRequest(token), "Bearer ${loginResponse.data!!.access}") }
        }
        return loginResponse
    }

    suspend fun logInWithGoogle(user: UserGoogleLogIn): Resource<UserLogInResponse> {
        val loginResponse = safeApiCall { api.logInWithGoogle(user) }
        if(loginResponse is Resource.Success){
            Log.d("Google Login", "Success")
        } else if(loginResponse is Resource.Error){
            loginResponse.message?.let { Log.d("Google Login", it) }
        }
        return safeApiCall { api.logInWithGoogle(user) }
    }

    suspend fun logOut(logoutRequest: RefreshToken, authorization: String): Resource<Message> {
        val logoutResponse = safeApiCall { api.logOut(logoutRequest, authorization = authorization) }
        if (logoutResponse is Resource.Success) {
            val token = FirebaseMessaging.getInstance().token.await()
            safeApiCall { api.unassignDeviceToken(DeviceTokenRequest(token),authorization) }
        }

        return logoutResponse
    }

    suspend fun refreshAccessToken(refreshToken: RefreshToken, authorization: String): Resource<AccessToken> {
        return safeApiCall { api.refreshAccessToken(refreshToken, authorization) }
    }

    suspend fun verifyOTP(request: VerifyRequest): Resource<Message> {
        return safeApiCall { api.verifyOTP(request) }
    }

    suspend fun verifyResetPasswordOTP(request: VerifyRequest): Resource<ResetToken> {
        return safeApiCall { api.verifyResetPasswordOTP(request) }
    }

    suspend fun checkEmail(email: Email): Resource<Message> {
        return safeApiCall { api.checkEmail(email) }
    }

    suspend fun changePassword(
        request: ChangePassword,
        authorization: String
    ): Resource<Message> = safeApiCall {
        api.changePassword(request, authorization)
    }

    suspend fun sendOPT(email: Email): Resource<Message> {
        return safeApiCall { api.sendOTP(email) }
    }

    suspend fun forgotPasswordSendOTP(email: Email): Resource<Message> {
        return safeApiCall { api.forgotPasswordSendOTP(email) }
    }

    suspend fun resetPassword(request: ResetPassword): Resource<Message> {
        return safeApiCall { api.resetPassword(request) }
    }

    suspend fun getUser(authorization: String): Resource<User>{
        return safeApiCall { api.getUser(authorization) }
    }

    suspend fun saveToken(
        accessToken: String,
        refreshToken: String,
        key1: Preferences.Key<String>,
        key2: Preferences.Key<String>
    ) {
        context.dataStore.edit { preferences ->
            preferences[key1] = accessToken
            preferences[key2] = refreshToken
        }
    }

    val accessToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }

    val refreshToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }

    suspend fun clearToken(key: Preferences.Key<String>) {
        context.dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }
}
