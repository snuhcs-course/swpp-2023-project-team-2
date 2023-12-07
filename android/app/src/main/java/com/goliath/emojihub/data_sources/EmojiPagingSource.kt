package com.goliath.emojihub.data_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.models.EmojiDto
import javax.inject.Inject

enum class EmojiFetchType {
    GENERAL, MY_CREATED, MY_SAVED
}

class EmojiPagingSource @Inject constructor(
    private val api: EmojiApi,
    private val sortByDate : Int,
    private val type: EmojiFetchType
): PagingSource<Int, EmojiDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, EmojiDto> {
        val cursor = params.key ?: 1
        val count = params.loadSize
        return try {
            val response: List<EmojiDto>? = when (type) {
                EmojiFetchType.GENERAL -> {
                    api.fetchEmojiList(sortByDate, cursor, count).body()
                }
                EmojiFetchType.MY_CREATED -> {
                    api.fetchMyCreatedEmojiList(sortByDate, cursor, count).body()
                }
                EmojiFetchType.MY_SAVED -> {
                    api.fetchMySavedEmojiList(sortByDate, cursor, count).body()
                }
            }
            val data = response ?: listOf()
            val nextKey = if (response?.size!! < count) null else cursor + 1 //Stops infinite fetching
            LoadResult.Page(
                data = data,
                prevKey = if (cursor == 1) null else cursor - 1,
                nextKey = nextKey
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