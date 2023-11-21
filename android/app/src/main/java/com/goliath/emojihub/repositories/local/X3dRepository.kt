package com.goliath.emojihub.repositories.local

import android.net.Uri
import android.util.Log
import com.goliath.emojihub.data_sources.local.X3dDataSource
import com.goliath.emojihub.models.CreatedEmoji
import org.pytorch.Module
import org.pytorch.Tensor
import javax.inject.Inject
import javax.inject.Singleton

interface X3dRepository {
    suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji>
}

@Singleton
class X3dRepositoryImpl @Inject constructor(
    private val x3dDataSource: X3dDataSource
): X3dRepository {
    companion object{
        const val SCORE_THRESHOLD = 0.4F
        // FIXME: Default emojis should be topK different emojis
        const val DEFAULT_EMOJI_NAME = "love it"
        const val DEFAULT_EMOJI_UNICODE = "U+2764 U+FE0F"
    }
    override suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji> {
        val x3dModule = x3dDataSource.loadModule("Hagrid/efficient_x3d_s_hagrid_float.pt")
            ?: return emptyList()
        val (classNameFilePath, classUnicodeFilePath) = x3dDataSource.checkAnnotationFilesExist(
            "Hagrid/hagrid_id_to_classname.json",
            "Hagrid/hagrid_classname_to_unicode.json"
        )?: return emptyList()
        val videoTensor = loadVideoTensor(videoUri) ?: return emptyList()
        return predictEmojiClass(
            x3dModule, videoTensor, classNameFilePath, classUnicodeFilePath, topK
        )
    }

    private fun loadVideoTensor(videoUri: Uri): Tensor? {
        val mediaMetadataRetriever =
            x3dDataSource.loadVideoMediaMetadataRetriever(videoUri) ?: return null
        return x3dDataSource.extractFrameTensorsFromVideo(mediaMetadataRetriever)
    }

    private fun predictEmojiClass(
        x3dModule: Module,
        videoTensor: Tensor,
        classNameFilePath: String,
        classUnicodeFilePath: String,
        topK: Int
    ): List<CreatedEmoji> {
        val inferenceResults = x3dDataSource.runInference(x3dModule, videoTensor, topK)
        if (inferenceResults.isEmpty() || inferenceResults[0].score < SCORE_THRESHOLD) {
            Log.w("X3d Repository", "Score is lower than threshold, return default emoji")
            return listOf(CreatedEmoji(DEFAULT_EMOJI_NAME, DEFAULT_EMOJI_UNICODE))
        }
        return x3dDataSource.indexToEmojiInfo(
            inferenceResults, classNameFilePath, classUnicodeFilePath
        )
    }
}