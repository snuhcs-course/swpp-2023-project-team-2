package com.goliath.emojihub.data_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.models.EmojiDto
import javax.inject.Inject

class EmojiPagingSource @Inject constructor(
    private val api: EmojiApi
): PagingSource<Int, EmojiDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EmojiDto> {
        val cursor = params.key ?: 1
        val count = params.loadSize
        return try {
            val response = api.fetchEmojiList(1, cursor, count).body()
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

    override fun getRefreshKey(state: PagingState<Int, EmojiDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}