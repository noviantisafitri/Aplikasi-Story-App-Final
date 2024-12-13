package com.dicoding.noviantisafitri.storyapp.di

import ApiService
import android.content.Context
import com.dicoding.noviantisafitri.storyapp.data.repository.StoriesRepository
import com.dicoding.noviantisafitri.storyapp.data.repository.UserRepository
import com.dicoding.noviantisafitri.storyapp.data.pref.UserPreference
import com.dicoding.noviantisafitri.storyapp.data.pref.dataStore
import com.dicoding.noviantisafitri.storyapp.data.remote.retrofit.ApiClient

object Injection {
    private fun provideUserPreference(context: Context): UserPreference {
        val dataStore = context.dataStore
        return UserPreference.getInstance(dataStore)
    }

    private fun provideApiService(): ApiService {
        return ApiClient.getApiService()
    }

    fun provideUserRepository(context: Context): UserRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiService()
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoriesRepository(context: Context): StoriesRepository {
        val pref = provideUserPreference(context)
        val apiService = provideApiService()
        return StoriesRepository.getInstance(pref, apiService)
    }
}