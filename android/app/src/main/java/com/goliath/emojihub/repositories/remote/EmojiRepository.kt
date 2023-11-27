package com.goliath.emojihub.repositories.remote

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.goliath.emojihub.data_sources.EmojiPagingSource
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.UploadEmojiDto
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    suspend fun fetchEmojiList(): Flow<PagingData<EmojiDto>>
    suspend fun getEmojiWithId(id: String): EmojiDto?
    suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Boolean
    suspend fun saveEmoji(id: String): Result<Unit>
    suspend fun unSaveEmoji(id: String): Result<Unit>
    suspend fun deleteEmoji(id: String): Response<Unit>
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    private val emojiApi: EmojiApi,
    @ApplicationContext private val context: Context
): EmojiRepository {
    override suspend fun fetchEmojiList(): Flow<PagingData<EmojiDto>> {
        return Pager(
            config = PagingConfig(pageSize = 10, initialLoadSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { EmojiPagingSource(emojiApi) }
        ).flow
    }

    override suspend fun getEmojiWithId(id: String): EmojiDto? {
        TODO("Not yet implemented")
    }

    override suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Boolean {
        val emojiDtoJson = Gson().toJson(emojiDto)
        val emojiDtoRequestBody = RequestBody.create("application/json".toMediaTypeOrNull(), emojiDtoJson)

        val videoFileRequestBody = RequestBody.create("video/mp4".toMediaTypeOrNull(), videoFile)
        val videoFileMultipartBody = MultipartBody.Part.createFormData("file", videoFile.name, videoFileRequestBody)

        val thumbnailFile = createVideoThumbnail(context, videoFile)

        val thumbnailRequestBody = RequestBody.create("image/jpg".toMediaTypeOrNull(),
            thumbnailFile!!
        )
        val thumbnailMultipartBody = MultipartBody.Part.createFormData("thumbnail", thumbnailFile?.name, thumbnailRequestBody)

        return try {
            emojiApi.uploadEmoji(videoFileMultipartBody, thumbnailMultipartBody, emojiDtoRequestBody)
            true
        }
        catch (e: IOException) {
            Log.d("EmojiRepository", "IOException")
            e.printStackTrace()
            false
        }
        catch (e: HttpException) {
            Log.d("EmojiRepository", "HttpException")
            e.printStackTrace()
            false
        }
    }

    override suspend fun saveEmoji(id: String): Result<Unit> {
//        return emojiApi.saveEmoji(id)
        return try {
            val response = emojiApi.saveEmoji(id)

            if (response.isSuccessful) {
                Log.d("SE", "success Emoji Id: $id")
                Result.success(Unit)
            } else {
                Log.d("SE", "fail Emoji Id: $id, ${response.code()}")
                Result.failure(RuntimeException("Error saving emoji"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unSaveEmoji(id: String): Result<Unit> {
        return try {
            val response = emojiApi.saveEmoji(id)

            if (response.isSuccessful) {
                Log.d("SE(unsave)", "success Emoji Id: $id")
                Result.success(Unit)
            } else {
                Log.d("SE(unsave)", "fail Emoji Id: $id, ${response.code()}")
                Result.failure(RuntimeException("Error saving emoji"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteEmoji(id: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    private fun createVideoThumbnail(context: Context, videoFile: File): File? {
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
            Log.d("create_TN", "ERROR...")
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return null
    }
}