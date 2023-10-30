package com.goliath.emojihub.repositories.local

import android.net.Uri
import android.util.Log
import com.goliath.emojihub.data_sources.local.X3dDataSource
import org.pytorch.Module
import org.pytorch.Tensor
import javax.inject.Inject
import javax.inject.Singleton

interface X3dRepository {
    fun createEmoji(videoUri: Uri): Pair<String, String>?
}

@Singleton
class X3dRepositoryImpl @Inject constructor(
    private val x3dDataSource: X3dDataSource
): X3dRepository {
    companion object{
        const val SCORE_THRESHOLD = 0.5F
    }
    override fun createEmoji(videoUri: Uri): Pair<String, String>? {
        val x3dModule = x3dDataSource.loadModule("efficient_x3d_xs_tutorial_float.pt")
            ?: return null
        val (classNameFilePath, classUnicodeFilePath) = x3dDataSource.checkAnnotationFilesExist()
            ?: return null
        val videoTensor = loadVideoTensor(videoUri) ?: return null
        return predictEmojiClass(x3dModule, videoTensor, classNameFilePath, classUnicodeFilePath)
    }

    private fun loadVideoTensor(videoUri: Uri): Tensor? {
        val mediaMetadataRetriever =
            x3dDataSource.loadVideoMediaMetadataRetriever(videoUri) ?: return null
        return x3dDataSource.extractFrameTensorsFromVideo(mediaMetadataRetriever)
    }

    private fun predictEmojiClass(
        x3dModule: Module, videoTensor: Tensor,
        classNameFilePath: String, classUnicodeFilePath: String
    ): Pair<String, String>? {
        val (maxScoreIdx, maxScore) = x3dDataSource.runInference(x3dModule, videoTensor)
        if (maxScore < SCORE_THRESHOLD) {
            Log.e("X3d Repository", "Score is lower than threshold")
            return null
        }
        return x3dDataSource.indexToEmojiInfo(
            maxScoreIdx, classNameFilePath, classUnicodeFilePath
        )
    }
}