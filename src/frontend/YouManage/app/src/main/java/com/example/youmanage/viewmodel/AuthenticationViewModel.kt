package com.example.youmanage.viewmodel

import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youmanage.data.remote.authentication.LogoutResponse
import com.example.youmanage.data.remote.authentication.RefreshToken
import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
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

    private val _logOutResponse = MutableLiveData<Resource<LogoutResponse>>()
    val logOutResponse: LiveData<Resource<LogoutResponse>> get() = _logOutResponse

    val accessToken: Flow<String?> = repository.accessToken
    val refreshToken: Flow<String?> = repository.refreshToken

    fun signUp(user: UserSignUp) {
        viewModelScope.launch {
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

    fun logOut(logoutRequest: RefreshToken, authorization: String) {
        viewModelScope.launch {
            _logOutResponse.value =
                repository.logOut(logoutRequest = logoutRequest, authorization = authorization)
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
}