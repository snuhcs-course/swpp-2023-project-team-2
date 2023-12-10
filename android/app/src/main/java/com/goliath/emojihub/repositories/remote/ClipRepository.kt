package com.goliath.emojihub.repositories.remote

import android.net.Uri
import android.util.Log
import com.goliath.emojihub.data_sources.ApiErrorController
import com.goliath.emojihub.data_sources.CustomError
import com.goliath.emojihub.data_sources.api.ClipApi
import com.goliath.emojihub.data_sources.local.MediaDataSource
import com.goliath.emojihub.models.ClipRequestDto
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.responses.ClipInferenceResponse
import javax.inject.Inject
import javax.inject.Singleton

interface ClipRepository {
    val DEFAULT_EMOJI_LIST: List<CreatedEmoji>
    suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji>
}

@Singleton
class ClipRepositoryImpl @Inject constructor(
    private val clipApi: ClipApi,
    private val mediaDataSource: MediaDataSource,
    private val errorController: ApiErrorController
): ClipRepository {
    // FIXME: Default emojis should be topK different emojis -> use just 3 emojis for now
    override val DEFAULT_EMOJI_LIST = listOf(
        CreatedEmoji("love it", "U+2764 U+FE0F"),
        CreatedEmoji("like", "U+1F44D"),
        CreatedEmoji("ok", "U+1F646")
    )
    companion object{
        // FIXME: Just use Hagrid for now
        const val classNameToUnicodeFileName = "zero_shot_classname_to_unicode.json"
    }

    override suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji> {
        Log.d("ClipRepository", "Creating emoji with videoUri: $videoUri and topK: $topK")
        // 1. Extract images from video
        val stringImageList = extractStringImageListFromVideo(videoUri, 1)
        if (stringImageList.isEmpty()) return emptyList()
        // 2. Run CLIP inference
        val inferenceResults = runClipInference(stringImageList, topK)
        if (inferenceResults.isEmpty()) return emptyList()
        // 3. Convert inference results to created emoji list
        return inferenceResultToCreatedEmojiList(inferenceResults)
    }

    fun extractStringImageListFromVideo(videoUri: Uri, numImages: Int): List<String> {
        Log.d("ClipRepository", "Extracting string image list from " +
                "videoUri: $videoUri with numImages: $numImages")
        val mediaMetadataRetriever =
            mediaDataSource.loadVideoMediaMetadataRetriever(videoUri) ?: return emptyList()
        val bitmapList = mediaDataSource.extractFrameImagesFromVideo(mediaMetadataRetriever, numImages)
        return bitmapList.map { mediaDataSource.bitmapToBase64Utf8(it) }
    }

    suspend fun runClipInference(
        imageList: List<String>, topK: Int
    ): List<ClipInferenceResponse> {
        Log.d("ClipRepository", "Loading class name to unicode json object: " +
                classNameToUnicodeFileName)
        val classNameToUnicodeJSONObject =
            mediaDataSource.getJSONObjectFromAssets(classNameToUnicodeFileName)
            ?: return emptyList() // Empty list if classUnicodeFilePath is invalid (ERROR)
        val classNameList = classNameToUnicodeJSONObject.keys().asSequence().toList()

        Log.d("ClipRepository", "Running clip inference with " +
                "imageList: $imageList and topK: $topK")
        val parameters = mapOf("candidate_labels" to classNameList)
        try {
            // FIXME: Use only 1 image for now
            for (image in imageList) {
                val response = clipApi.runClipInference(ClipRequestDto(image, parameters))
                if (response.isSuccessful) {
                    val inferenceResponseList = response.body()?: return emptyList()
                    Log.d("ClipRepository", "Clip inference response is successful " +
                        "with inferenceResponseList: $inferenceResponseList")
                    return inferenceResponseList.take(topK).map { inferenceResponse ->
                        ClipInferenceResponse(inferenceResponse)
                    }
                }
            }
            Log.e("ClipRepository", "Clip inference response is not successful")
            errorController.setErrorState(CustomError.NETWORK_IS_BUSY.statusCode)
            return emptyList()
        } catch (e: Exception) {
            Log.e("ClipRepository", "Error running clip inference ${e.message}")
            errorController.setErrorState(CustomError.NETWORK_IS_BUSY.statusCode)
        }
        return emptyList()
    }

    fun inferenceResultToCreatedEmojiList(
        inferenceResults: List<ClipInferenceResponse>
    ): List<CreatedEmoji> {
        Log.d("ClipRepository", "Converting inference results " +
            "to created emoji list with inferenceResults: $inferenceResults")
        val classUnicodeJSONObject =
            mediaDataSource.getJSONObjectFromAssets(classNameToUnicodeFileName)?:
            return emptyList() // Empty list if classUnicodeFilePath is invalid (ERROR)

        val createdEmojiList = mutableListOf<CreatedEmoji>()
        return try {
            for (result in inferenceResults) {
                val className = result.emojiName
                val classUnicode = classUnicodeJSONObject.getString(className)
                createdEmojiList.add(CreatedEmoji(className, classUnicode))
            }
            createdEmojiList
        } catch (e: Exception) {
            Log.e("ClipRepository", "Error loading class name or unicode ${e.message}")
            emptyList()
        }
    }
}