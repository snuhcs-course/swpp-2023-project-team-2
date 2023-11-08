package com.goliath.emojihub.repositories.remote

import android.util.Log
import com.goliath.emojihub.data_sources.api.EmojiApi
import com.goliath.emojihub.models.EmojiDto
import com.goliath.emojihub.models.uploadEmojiDto
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
    suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean
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
    override suspend fun uploadEmoji(emojiUnicode: String, emojiLabel: String, videoFile: File): Boolean {
        val emojiUnicodeBody = MultipartBody.Part.createFormData("emoji_unicode", emojiUnicode)
        val emojiLabelBody = MultipartBody.Part.createFormData("emoji_label", emojiLabel)

        val requestVideoFile = RequestBody.create(MediaType.parse("video/*"), videoFile)
        val videoFileBody = MultipartBody.Part.createFormData("file", videoFile.name, requestVideoFile)

        return try {
            Log.d("EmojiRepository", "emojiUnicodeBody: $emojiUnicodeBody")
            Log.d("EmojiRepository", "emojiLabelBody: $emojiLabelBody")
            Log.d("EmojiRepository", "videoFileBody: $videoFileBody")
            EmojiApi.instance.uploadEmoji(emojiUnicodeBody, emojiLabelBody, videoFileBody)
            true
        }
        catch (e: IOException) {
            e.printStackTrace()
            false
        }
        catch (e: HttpException) {
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