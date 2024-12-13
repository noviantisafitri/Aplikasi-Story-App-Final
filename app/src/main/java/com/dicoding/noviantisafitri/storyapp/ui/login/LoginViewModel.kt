package com.dicoding.noviantisafitri.storyapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.noviantisafitri.storyapp.common.Resource
import com.dicoding.noviantisafitri.storyapp.data.repository.UserRepository
import com.dicoding.noviantisafitri.storyapp.data.pref.UserModel
import com.dicoding.noviantisafitri.storyapp.data.remote.request.LoginRequest
import com.dicoding.noviantisafitri.storyapp.data.remote.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun loginUser(email: String, password: String, onResult: (Resource<LoginResponse?>) -> Unit) {
        viewModelScope.launch {
            onResult(Resource.Loading())
            try {
                val response = repository.loginUser(LoginRequest(email, password))
                response?.loginResult?.let {
                    saveSession(UserModel(it.name ?: "", it.token ?: ""))
                }
                onResult(Resource.Success(response))
            } catch (e: Exception) {
                onResult(Resource.Error("Login failed"))
            }
        }
    }


    private fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}