package com.goliath.emojihub.data_sources.local

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

interface MediaDataSource {
    fun loadVideoMediaMetadataRetriever(videoUri: Uri): MediaMetadataRetriever?
    fun extractFrameImagesFromVideo(
        mediaMetadataRetriever: MediaMetadataRetriever,
        numImages: Int
    ): List<Bitmap>
    fun bitmapToBase64Utf8(bitmap: Bitmap): String
    fun getClassToUnicodeJSONObject(classToUnicodeFileName: String): JSONObject?
}

@Singleton
class MediaDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): MediaDataSource {

    companion object{
        private const val FRAMES_PER_SECOND = 30 // FIXME: Maybe use 24, 60 fps instead?
    }

    override fun loadVideoMediaMetadataRetriever(videoUri: Uri): MediaMetadataRetriever? {
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, videoUri)
            if ((mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLong() ?: 0) <= 0
            ) {
                Log.e("X3dDataSource", "Video file is invalid")
                return null
            }
            return mediaMetadataRetriever
        } catch (e: Exception) {
            Log.e("X3dDataSource", "Error loading video media metadata retriever ${e.message}")
        }
        return null
    }

    override fun extractFrameImagesFromVideo(
        mediaMetadataRetriever: MediaMetadataRetriever,
        numImages: Int
    ): List<Bitmap> {
        try {
            val videoLengthInMs = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: return emptyList()

            val videoLengthInSec = videoLengthInMs / 1000
            val numFrames = videoLengthInSec * FRAMES_PER_SECOND
            val frameInterval = (numFrames / numImages)

            val frameImages = mutableListOf<Bitmap>()
            for (i in 0 until numImages) {
                val frameTime = i * frameInterval
                val bitmap = mediaMetadataRetriever.getFrameAtTime(
                    frameTime,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                if (bitmap != null) {
                    frameImages.add(bitmap)
                }
            }
            return frameImages
        } catch (e: Exception) {
            Log.e("MediaDataSource", "Error extracting frame images from video ${e.message}")
        }
        return emptyList()
    }

    override fun bitmapToBase64Utf8(bitmap: Bitmap): String {
        // bitmap to byte array
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        // byte array to base64 string
        return Base64.getEncoder().encodeToString(byteArray)
    }

    override fun getClassToUnicodeJSONObject(classToUnicodeFileName: String): JSONObject? {
        try {
            val classToUnicodeFile = context.assets.open(classToUnicodeFileName)
            val classToUnicodeString = classToUnicodeFile.bufferedReader().use { it.readText() }
            return JSONObject(classToUnicodeString)
        } catch (e: Exception) {
            Log.e("MediaDataSource", "Error loading class to unicode json object ${e.message}")
        }
        return null
    }
}