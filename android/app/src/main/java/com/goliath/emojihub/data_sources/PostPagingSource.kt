package com.goliath.emojihub.data_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.goliath.emojihub.data_sources.api.PostApi
import com.goliath.emojihub.models.PostDto
import javax.inject.Inject

enum class PostFetchType {
    GENERAL, MY
}

class PostPagingSource @Inject constructor(
    private val api: PostApi,
    private val type: PostFetchType
): PagingSource<Int, PostDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostDto> {
        val cursor = params.key ?: 1
        return try {
            val response: List<PostDto>? = when (type) {
                PostFetchType.GENERAL -> {
                    api.fetchPostList(cursor).body()
                }
                PostFetchType.MY -> {
                    api.fetchPostList(cursor).body()
                }
            }
            val data = response ?: listOf()
            LoadResult.Page(
                data = data,
                prevKey = if (cursor == 1) null else cursor - 1,
                nextKey = if (data.isEmpty()) null else cursor + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PostDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}