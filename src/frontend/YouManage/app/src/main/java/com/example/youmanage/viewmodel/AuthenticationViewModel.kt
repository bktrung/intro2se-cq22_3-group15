package com.example.youmanage.viewmodel

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.authentication.AccessToken
import com.example.youmanage.data.remote.authentication.ChangePasswordRequest
import com.example.youmanage.data.remote.authentication.Email
import com.example.youmanage.data.remote.authentication.Message
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.ResetToken
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import com.example.youmanage.data.remote.authentication.VerifyRequest
import com.example.youmanage.data.remote.projectmanagement.User
import com.example.youmanage.repository.AuthenticationRepository
import com.example.youmanage.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthenticationRepository
) : ViewModel() {

    private val _signUpResponse = MutableLiveData<Resource<UserSignUpResponse>>()
    val signUpResponse: LiveData<Resource<UserSignUpResponse>> get() = _signUpResponse

    private val _logInResponse = MutableLiveData<Resource<UserLogInResponse>>()
    val logInResponse: LiveData<Resource<UserLogInResponse>> get() = _logInResponse

    private val _logOutResponse = MutableLiveData<Resource<Message>>()
    val logOutResponse: LiveData<Resource<Message>> get() = _logOutResponse

    private val _verifyOTPResponse = MutableLiveData<Resource<Message>>()
    val verifyOTPResponse: LiveData<Resource<Message>> get() = _verifyOTPResponse

    private val _verifyResetPasswordOTP = MutableLiveData<Resource<ResetToken>>()
    val verifyResetPasswordOTP: LiveData<Resource<ResetToken>> get() = _verifyResetPasswordOTP

    private val _message = MutableLiveData<Resource<Message>>()
    val message: LiveData<Resource<Message>> get() = _message

    private val _user = MutableLiveData<Resource<User>>()
    val user : LiveData<Resource<User>> get() = _user

    private val _refreshResponse = MutableLiveData<Resource<AccessToken>>()
    val refreshResponse: LiveData<Resource<AccessToken>> get() = _refreshResponse

    val accessToken: Flow<String?> = repository.accessToken
    val refreshToken: Flow<String?> = repository.refreshToken

    fun signUp(user: UserSignUp) {
        viewModelScope.launch {
            _signUpResponse.value = Resource.Loading(UserSignUpResponse("", ""))
            _signUpResponse.value = repository.signUp(user)
        }
    }

    fun logIn(user: UserLogIn) {
        viewModelScope.launch {
            _logInResponse.value = repository.logIn(user)
        }
    }

    fun logInWithGoogle(user: UserGoogleLogIn) {
        viewModelScope.launch {
            _logInResponse.value = repository.logInWithGoogle(user)
        }
    }

    fun verifyOTP(request: VerifyRequest){
        viewModelScope.launch {
            _verifyOTPResponse.value = repository.verifyOTP(request)
        }
    }

    fun refreshAccessToken(refreshToken: RefreshToken, authorization: String) {
        viewModelScope.launch {
            _refreshResponse.value = repository.refreshAccessToken(refreshToken, authorization)
        }
    }

    fun verifyResetPasswordOTP(request: VerifyRequest){
        viewModelScope.launch {
            _verifyResetPasswordOTP.value = repository.verifyResetPasswordOTP(request)
        }
    }
    fun sendOTP(email: Email){
        viewModelScope.launch {
            repository.sendOPT(email)
        }
    }

    fun checkEmail(email: Email){
        viewModelScope.launch {
            _message.value = repository.checkEmail(email)
        }
    }

    fun forgotPasswordSendOTP(email: Email){
        viewModelScope.launch {
           _verifyOTPResponse.value = repository.forgotPasswordSendOTP(email)
        }
    }

    fun logOut(logoutRequest: RefreshToken, authorization: String) {
        viewModelScope.launch {
            _logOutResponse.value =
                repository.logOut(logoutRequest = logoutRequest, authorization = authorization)
        }
    }

    fun changePassword(request: ChangePasswordRequest){
        viewModelScope.launch {
            Log.d("changePassword", "changePassword: 1")
            _message.value = repository.changePassword(request)
        }
    }

    fun saveToken(
        accessToken: String,
        refreshToken: String,
        key1: Preferences.Key<String>,
        key2: Preferences.Key<String>
    ) {
        viewModelScope.launch {
            repository.saveToken(accessToken, refreshToken, key1, key2)
        }
    }

    fun clearToken(key: Preferences.Key<String>) {
        viewModelScope.launch {
            repository.clearToken(key)
        }
    }

    fun getUser(authorization: String){
        viewModelScope.launch {
            _user.value = repository.getUser(authorization)
        }
    }
}