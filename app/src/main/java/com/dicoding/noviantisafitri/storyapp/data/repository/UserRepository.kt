package com.dicoding.noviantisafitri.storyapp.data.repository

import ApiService
import com.dicoding.noviantisafitri.storyapp.data.pref.UserModel
import com.dicoding.noviantisafitri.storyapp.data.pref.UserPreference
import com.dicoding.noviantisafitri.storyapp.data.remote.request.LoginRequest
import com.dicoding.noviantisafitri.storyapp.data.remote.request.RegisterRequest
import com.dicoding.noviantisafitri.storyapp.data.remote.response.LoginResponse
import com.dicoding.noviantisafitri.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow


class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {


    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun registerUser(request: RegisterRequest): RegisterResponse {
        return apiService.registerUser(request)
    }

    suspend fun loginUser(loginRequest: LoginRequest): LoginResponse? {
        return try {
            apiService.loginUser(loginRequest)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}