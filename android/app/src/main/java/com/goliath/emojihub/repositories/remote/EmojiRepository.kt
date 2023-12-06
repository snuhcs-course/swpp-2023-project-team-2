package com.goliath.emojihub.repositories.remote

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.EmojiFetchType
import com.goliath.emojihub.data_sources.EmojiPagingSource
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.UploadEmojiDto
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    suspend fun fetchEmojiList(sortByDate: Int): Flow<PagingData<EmojiDto>>
    suspend fun fetchMyCreatedEmojiList(): Flow<PagingData<EmojiDto>>
    suspend fun fetchMySavedEmojiList(): Flow<PagingData<EmojiDto>>
    suspend fun getEmojiWithId(id: String): EmojiDto?
    suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Boolean
    suspend fun saveEmoji(id: String): Response<Unit>
    suspend fun unSaveEmoji(id: String): Response<Unit>
    suspend fun deleteEmoji(id: String): Response<Unit>
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    private val emojiApi: EmojiApi,
    @ApplicationContext private val context: Context
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

    override suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Boolean {
        val emojiDtoJson = Gson().toJson(emojiDto)
        val emojiDtoRequestBody = emojiDtoJson.toRequestBody("application/json".toMediaTypeOrNull())

        val videoFileRequestBody = videoFile.asRequestBody("video/mp4".toMediaTypeOrNull())
        val videoFileMultipartBody = MultipartBody.Part.createFormData("file", videoFile.name, videoFileRequestBody)

        val thumbnailFile = createVideoThumbnail(context, videoFile)
        val thumbnailRequestBody = thumbnailFile!!
            .asRequestBody("image/jpg".toMediaTypeOrNull())
        val thumbnailMultipartBody = MultipartBody.Part.createFormData("thumbnail",
            thumbnailFile.name, thumbnailRequestBody)

        return try {
            emojiApi.uploadEmoji(videoFileMultipartBody, thumbnailMultipartBody, emojiDtoRequestBody)
            true
        }
        catch (e: IOException) {
            Log.e("EmojiRepository", "IOException")
            false
        }
        catch (e: HttpException) {
            Log.e("EmojiRepository", "HttpException")
            false
        }
        catch (e: Exception) {
            Log.e("EmojiRepository", "Unknown Exception: ${e.message}")
            false
        }
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

    fun createVideoThumbnail(context: Context, videoFile: File): File? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoFile.absolutePath)
            val bitmap = retriever.frameAtTime

            bitmap?.let {
                val thumbnailFile = File(context.cacheDir, "thumbnail_${videoFile.name}.jpg")
                FileOutputStream(thumbnailFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                }
                Log.d("create_TN", "Thumbnail created: ${thumbnailFile.absolutePath}")
                return thumbnailFile
            }
        } catch (e: Exception) {
            Log.e("EmojiRepository_create_TN", "ERROR creating thumbnail: ${e.message?:"Unknown error"}")
        } finally {
            retriever.release()
        }
        return null
    }
}