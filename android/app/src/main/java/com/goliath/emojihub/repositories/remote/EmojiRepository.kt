package com.goliath.emojihub.repositories.remote

import android.util.Log
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.UploadEmojiDto
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface EmojiRepository {
    suspend fun fetchEmojiList(numLimit: Int): List<EmojiDto>
    suspend fun getEmojiWithId(emojiId: String): EmojiDto?
    suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Boolean
    suspend fun saveEmoji(emojiId: String)
    suspend fun unSaveEmoji(emojiId: String)
    suspend fun deleteEmoji()
}

@Singleton
class EmojiRepositoryImpl @Inject constructor(
    private val emojiApi: EmojiApi
): EmojiRepository {
    override suspend fun fetchEmojiList(numLimit: Int): List<EmojiDto> {
        TODO("Not yet implemented")
    }
    override suspend fun getEmojiWithId(emojiId: String): EmojiDto? {
        TODO("Not yet implemented")
    }
    override suspend fun uploadEmoji(videoFile: File, emojiDto: UploadEmojiDto): Boolean {
        //val emojiUnicodeBody = MultipartBody.Part.createFormData("emoji_unicode", emojiUnicode)
        //val emojiLabelBody = MultipartBody.Part.createFormData("emoji_label", emojiLabel)

        val emojiDtoJson = Gson().toJson(emojiDto)
        val requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), emojiDtoJson)
        val emojiDtoBody = MultipartBody.Part.createFormData("jsonObject", null, requestBody)

        val requestVideoFile = RequestBody.create(MediaType.parse("video/mp4"), videoFile)
        val videoFileBody = MultipartBody.Part.createFormData("file", videoFile.name, requestVideoFile)

        return try {
//            Log.d("EmojiRepository", "emojiDtoBody: ${emojiDtoBody.body().contentType()}")
//            Log.d("EmojiRepository", "videoFileBody: ${videoFileBody.body().contentType()}")
            emojiApi.uploadEmoji(videoFileBody, requestBody)
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
    override suspend fun saveEmoji(emojiId: String) {
        TODO("Not yet implemented")
    }
    override suspend fun unSaveEmoji(emojiId: String) {
        TODO("Not yet implemented")
    }
    override suspend fun deleteEmoji() {
        TODO("Not yet implemented")
    }
}