package com.goliath.emojihub.repositories.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.EmojiFetchType
import com.goliath.emojihub.data_sources.EmojiPagingSource
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.data_sources.remote.EmojiDataSource
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.UploadEmojiDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    suspend fun fetchEmojiList(sortByDate: Int): Flow<PagingData<EmojiDto>>
    suspend fun fetchMyCreatedEmojiList(): Flow<PagingData<EmojiDto>>
    suspend fun fetchMySavedEmojiList(): Flow<PagingData<EmojiDto>>
    suspend fun getEmojiWithId(id: String): EmojiDto?
    suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Response<Unit>
    suspend fun saveEmoji(id: String): Response<Unit>
    suspend fun unSaveEmoji(id: String): Response<Unit>
    suspend fun deleteEmoji(id: String): Response<Unit>
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    private val emojiApi: EmojiApi,
    private val emojiDataSource: EmojiDataSource
): EmojiRepository {
    override suspend fun fetchEmojiList(sortByDate: Int): Flow<PagingData<EmojiDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { EmojiPagingSource(emojiApi, sortByDate, EmojiFetchType.GENERAL) }
        ).flow
    }

    override suspend fun fetchMyCreatedEmojiList(): Flow<PagingData<EmojiDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { EmojiPagingSource(emojiApi, 1, EmojiFetchType.MY_CREATED) }
        ).flow
    }

    override suspend fun fetchMySavedEmojiList(): Flow<PagingData<EmojiDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { EmojiPagingSource(emojiApi, 1, EmojiFetchType.MY_SAVED) }
        ).flow
    }

    override suspend fun getEmojiWithId(id: String): EmojiDto? {
        TODO("Not yet implemented")
    }

    override suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Response<Unit> {
        val emojiDtoJson = Gson().toJson(emojiDto)
        val emojiDtoRequestBody = emojiDtoJson.toRequestBody("application/json".toMediaTypeOrNull())

        val videoFileRequestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        val videoFileMultipartBody = MultipartBody.Part.createFormData("file", videoFile.name, videoFileRequestBody)

        val thumbnailFile = emojiDataSource.createVideoThumbNail(videoFile)
        val thumbnailRequestBody = thumbnailFile!!
            .asRequestBody("image/jpg".toMediaTypeOrNull())
        val thumbnailMultipartBody = MultipartBody.Part.createFormData("thumbnail",
            thumbnailFile.name, thumbnailRequestBody)

        return emojiApi.uploadEmoji(videoFileMultipartBody, thumbnailMultipartBody, emojiDtoRequestBody)
    }

    override suspend fun saveEmoji(id: String): Response<Unit> {
        return emojiApi.saveEmoji(id)
    }

    override suspend fun unSaveEmoji(id: String): Response<Unit> {
        return emojiApi.unSaveEmoji(id)
    }

    override suspend fun deleteEmoji(id: String): Response<Unit> {
        TODO("Not yet implemented")
    }
}