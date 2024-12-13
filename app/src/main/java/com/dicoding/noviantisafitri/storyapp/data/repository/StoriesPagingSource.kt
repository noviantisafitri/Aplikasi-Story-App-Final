package com.dicoding.noviantisafitri.storyapp.data.repository

import ApiService
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.noviantisafitri.storyapp.data.pref.UserPreference
import com.dicoding.noviantisafitri.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.firstOrNull

class StoriesPagingSource(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val location: Int? = null
) : PagingSource<Int, ListStoryItem>() {

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { position ->
            val closestPage = state.closestPageToPosition(position)
            closestPage?.prevKey?.plus(1) ?: closestPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        val token = userPreference.getToken().firstOrNull()

        if (token.isNullOrEmpty()) {
            return LoadResult.Error(Exception("Token not found"))
        }

        return try {
            val response = fetchStories(position, params.loadSize, token)
            val stories = response?.listStory?.filterNotNull().orEmpty()

            LoadResult.Page(
                data = stories,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (stories.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private suspend fun fetchStories(page: Int, size: Int, token: String) =
        location?.let {
            apiService.getStoriesAuth(
                "Bearer $token",
                page = page,
                size = size,
                location = it
            )
        }
}