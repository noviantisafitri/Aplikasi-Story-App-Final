package com.dicoding.noviantisafitri.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.dicoding.noviantisafitri.storyapp.data.remote.response.ListStoryItem
import com.dicoding.noviantisafitri.storyapp.data.remote.response.StoriesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface IStoriesRepository {
    fun getStoriesStream(location: Int?): LiveData<PagingData<ListStoryItem>>
    suspend fun postStoryAuth(
        description: RequestBody,
        photoFile: MultipartBody.Part,
        latitude: RequestBody? = null,
        longitude: RequestBody? = null
    ): StoriesResponse
    suspend fun getStories(token: String, location: Int): StoriesResponse
}
