package com.example.youmanage.data.remote

import com.example.youmanage.data.remote.authentication.UserGoogleLogIn
import com.example.youmanage.data.remote.authentication.UserLogIn
import com.example.youmanage.data.remote.authentication.UserLogInResponse
import com.example.youmanage.data.remote.authentication.UserSignUp
import com.example.youmanage.data.remote.authentication.UserSignUpResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {

    @POST("auth/signup/")
    suspend fun signUp(
        @Body user: UserSignUp
    ): UserSignUpResponse

    @POST("auth/login/")
    suspend fun logIn(
        @Body user: UserLogIn
    ): UserLogInResponse

    @POST("auth/google-login/")
    suspend fun logInWithGoogle(
        @Body user: UserGoogleLogIn
    ): UserLogInResponse


}