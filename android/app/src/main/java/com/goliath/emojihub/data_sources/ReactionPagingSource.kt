package com.goliath.emojihub.data_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.goliath.emojihub.data_sources.api.ReactionApi
import com.goliath.emojihub.models.ReactionWithEmojiDto
import javax.inject.Inject

class ReactionPagingSource @Inject constructor(
    private val api: ReactionApi,
    private val postId: String,
    private val emojiUnicode: String
): PagingSource<Int, ReactionWithEmojiDto>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ReactionWithEmojiDto> {
        val cursor = params.key ?: 1
        val count = params.loadSize
        return try {
            val response: List<ReactionWithEmojiDto>? = api.fetchReactionList(postId, emojiUnicode, cursor, count).body()
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

    override fun getRefreshKey(state: PagingState<Int, ReactionWithEmojiDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
