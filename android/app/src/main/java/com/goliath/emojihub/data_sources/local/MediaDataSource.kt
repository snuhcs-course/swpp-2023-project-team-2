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
    fun getJSONObjectFromAssets(fileName: String): JSONObject?
}

@Singleton
class MediaDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): MediaDataSource {

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
            val numFrames = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT
            )?.toLong() ?: return emptyList()
            Log.d("MediaDataSource", "Video has $numFrames frames")
            val frameInterval = (numFrames / (numImages + 1))
            Log.d("MediaDataSource", "Frame will be extracted every $frameInterval " +
                    "/ $numFrames (frames / 1 image) for numImages: $numImages times")

            val frameImages = mutableListOf<Bitmap>()
            for (i in 1 until numImages + 1) {
                val frameTime = i * frameInterval
                val bitmap = mediaMetadataRetriever.getFrameAtTime(
                    frameTime,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                if (bitmap != null) {
                    Log.d("MediaDataSource", "Extracted $i th frame image")
                    frameImages.add(bitmap)
                }
            }
            Log.i("MediaDataSource", "Expected numImages: $numImages, " +
                    "Actual frameImages.size: ${frameImages.size}")
            if (frameImages.isEmpty()) {
                Log.e("MediaDataSource", "0 images extracted from video")
                return emptyList()
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

    override fun getJSONObjectFromAssets(fileName: String): JSONObject? {
        try {
            val classToUnicodeFile = context.assets.open(fileName)
            val classToUnicodeString = classToUnicodeFile.bufferedReader().use { it.readText() }
            return JSONObject(classToUnicodeString)
        } catch (e: Exception) {
            Log.e("MediaDataSource", "Error loading json object from assets ${e.message}")
        }
        return null
    }
}