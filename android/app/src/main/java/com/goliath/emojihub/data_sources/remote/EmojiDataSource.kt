package com.goliath.emojihub.data_sources.remote

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

interface EmojiDataSource {
    fun createVideoThumbNail(videoFile: File): File?
}
class EmojiDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): EmojiDataSource {

    override fun createVideoThumbNail(videoFile: File): File? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoFile.absolutePath)
            val bitmap = retriever.frameAtTime

            bitmap?.let {
                val thumbnailFile = File(context.cacheDir, "thumbnail_${videoFile.name}.jpg")
                FileOutputStream(thumbnailFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 75, out)
                }
                Log.d("EmojiDataSource", "Thumbnail created: ${thumbnailFile.absolutePath}")
                return thumbnailFile
            }
        } catch (e: Exception) {
            Log.e("EmojiDataSource", "ERROR creating thumbnail: ${e.message?:"Unknown error"}")
        } finally {
            retriever.release()
        }
        return null
    }
}