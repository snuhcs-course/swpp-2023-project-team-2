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
    val DEFAULT_EMOJI_LIST: List<CreatedEmoji>
    suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji>
}

@Singleton
class X3dRepositoryImpl @Inject constructor(
    private val x3dDataSource: X3dDataSource
): X3dRepository {

    // FIXME: Default emojis should be topK different emojis -> use just 3 emojis for now
    override val DEFAULT_EMOJI_LIST = listOf(
        CreatedEmoji("love it", "U+2764 U+FE0F"),
        CreatedEmoji("like", "U+1F44D"),
        CreatedEmoji("ok", "U+1F646")
    )
    companion object{
        const val moduleName = "Hagrid/efficient_x3d_s_hagrid_float.pt"
        const val idToClassFileName = "Hagrid/hagrid_id_to_classname.json"
        const val classToUnicodeFileName = "Hagrid/hagrid_classname_to_unicode.json"
        const val SCORE_THRESHOLD = 0.4F
    }

    override suspend fun createEmoji(videoUri: Uri, topK: Int): List<CreatedEmoji> {
        val x3dModule = x3dDataSource.loadModule(moduleName)
            ?: return emptyList()
        val (classNameFilePath, classUnicodeFilePath) = x3dDataSource.checkAnnotationFilesExist(
            idToClassFileName, classToUnicodeFileName)?: return emptyList()
        val videoTensor = loadVideoTensor(videoUri) ?: return emptyList()
        return predictEmojiClass(
            x3dModule, videoTensor, classNameFilePath, classUnicodeFilePath, topK
        )
    }

    fun loadVideoTensor(videoUri: Uri): Tensor? {
        val mediaMetadataRetriever =
            x3dDataSource.loadVideoMediaMetadataRetriever(videoUri) ?: return null
        return x3dDataSource.extractFrameTensorsFromVideo(mediaMetadataRetriever)
    }

    fun predictEmojiClass(
        x3dModule: Module,
        videoTensor: Tensor,
        idToClassFileName: String,
        classToUnicodeFileName: String,
        topK: Int
    ): List<CreatedEmoji> {
        val inferenceResults = x3dDataSource.runInference(x3dModule, videoTensor, topK)
        if (inferenceResults.isEmpty() || inferenceResults[0].score < SCORE_THRESHOLD) {
            Log.w("X3d Repository", "Score is lower than threshold, return default emoji")
            return DEFAULT_EMOJI_LIST
        }
        return x3dDataSource.indexToCreatedEmojiList(
            inferenceResults, idToClassFileName, classToUnicodeFileName
        )
    }
}