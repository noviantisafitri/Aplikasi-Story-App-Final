package com.dicoding.noviantisafitri.storyapp.ui

import MainViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.noviantisafitri.storyapp.data.repository.StoriesRepository
import com.dicoding.noviantisafitri.storyapp.data.repository.UserRepository
import com.dicoding.noviantisafitri.storyapp.di.Injection
import com.dicoding.noviantisafitri.storyapp.ui.login.LoginViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storiesRepository: StoriesRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main // Default ke Main dispatcher
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository, storiesRepository, dispatcher) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context, dispatcher: CoroutineDispatcher = Dispatchers.Main): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideStoriesRepository(context),
                    dispatcher
                ).also { INSTANCE = it }
            }
        }
    }
}
