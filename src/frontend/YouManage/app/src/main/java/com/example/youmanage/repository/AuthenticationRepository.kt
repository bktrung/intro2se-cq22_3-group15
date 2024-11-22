package com.example.youmanage.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.youmanage.data.remote.ApiInterface
import com.example.youmanage.data.remote.authentication.ChangePasswordRequest
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
import com.example.youmanage.utils.Constants.ACCESS_TOKEN_KEY
import com.example.youmanage.utils.Constants.REFRESH_TOKEN_KEY
import com.example.youmanage.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessToken")

@ActivityScoped
class AuthenticationRepository @Inject constructor(
    private val api: ApiInterface,
    @ApplicationContext private val context: Context
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
            } else if (e.code() == 401) {
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

    suspend fun logOut(
        logoutRequest: RefreshToken,
        authorization: String
    ): Resource<Message> {
        val response = try {
            Resource.Success(
                api.logOut(
                    logoutRequest = logoutRequest,
                    authorization = authorization
                )
            )
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

    suspend fun verifyOTP(request: VerifyRequest): Resource<Message> {
        val response = try {
            Resource.Success(api.verifyOTP(request))
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

    suspend fun verifyResetPasswordOTP(request: VerifyRequest): Resource<ResetToken> {
        val response = try {
            Resource.Success(api.verifyResetPasswordOTP(request))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error("Invalid OTP")
            } else if (e.code() == 404) {
                Resource.Error("User not found")
            } else {
                Resource.Error("HTTP Error: ${e.code()}")

            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }
        return response
    }

    suspend fun checkEmail(email: Email): Resource<Message> {
        val response = try {
            Resource.Success(api.checkEmail(email))
        } catch (e: HttpException) {
            if (e.code() == 400) {
                Resource.Error("Email not found or not yet verified.")
            } else if (e.code() == 404) {
                Resource.Error("User not found in the system.")
            } else {
                Resource.Error("HTTP Error: ${e.code()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message.toString())
        }

        return response
    }

    suspend fun sendOPT(email: Email): Resource<Message> {
        val response = try {
            Resource.Success(api.sendOTP(email))
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

    suspend fun forgotPasswordSendOTP(email: Email): Resource<Message> {
        val response = try {
            Resource.Success(api.forgotPasswordSendOTP(email))
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

    suspend fun changePassword(request: ChangePasswordRequest): Resource<Message> {
        val response = try {
            Log.d("changePassword", "changePassword: $request")
            Resource.Success(api.changePassword(request))
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