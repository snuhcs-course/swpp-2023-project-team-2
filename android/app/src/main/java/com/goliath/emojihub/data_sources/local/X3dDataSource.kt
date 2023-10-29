package com.goliath.emojihub.data_sources.local

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.Tensor
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp

interface X3dDataSource {
    fun loadModule(moduleName: String): Module?
    fun checkAnnotationFilesExist(): Pair<String, String>?
    fun loadVideoMediaMetadataRetriever(videoUri: Uri): MediaMetadataRetriever?
    fun extractFrameTensorsFromVideo(mediaMetadataRetriever: MediaMetadataRetriever): Tensor?
    fun runInference(x3dModule: Module, videoTensor: Tensor): Pair<Int, Float>
    fun indexToEmojiInfo(
        maxScoreIdx: Int,
        classNameFilePath: String,
        classUnicodeFilePath: String
    ): Pair<String, String>?
    fun assetFilePath(assetName: String): String
}

@Singleton
class X3dDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
): X3dDataSource {
    companion object{
        /*
         !IMPORTANT! : CROP_SIZE, COUNT_OF_FRAMES_PER_INFERENCE should be same as
                      those specified when converting Pytorch model to TorchScript
                      In tutorial jupyter notebook (pytorchvideo/tutorials/accelerator/Use_PytorchVideo_Accelerator_Model_Zoo.ipynb),
                      CROP_SIZE = 160, COUNT_OF_FRAMES_PER_INFERENCE = 4
         !IMPORTANT! : SAMPLING_RATE should be same as X3D config
                      from X3D paper https://arxiv.org/pdf/2004.04730.pdf
                     for XS expansion - SAMPLING_RATE = 12, COUNT_OF_FRAMES_PER_INFERENCE = 4
                     for S expansion - SAMPLING_RATE = 6, COUNT_OF_FRAMES_PER_INFERENCE = 13
         TODO: refer to the original paper, apply dimension parameters
              X-fast, X-temporal, X-spatial, X-Depth, X-Width,X-Bottleneck
        */
        private const val SIDE_SIZE = 160
        val MEAN = floatArrayOf(0.45F, 0.45F, 0.45F)
        val STD = floatArrayOf(0.225F, 0.225F, 0.225F)
        const val CROP_SIZE = 160
        const val NUM_CHANNELS = 3
        private const val FRAMES_PER_SECOND = 30
        private const val SAMPLING_RATE = 12
        const val COUNT_OF_FRAMES_PER_INFERENCE = 4
        private const val MODEL_INPUT_SIZE = COUNT_OF_FRAMES_PER_INFERENCE * NUM_CHANNELS * CROP_SIZE * CROP_SIZE
    }

    override fun loadModule(moduleName: String): Module? {
        try {
            return Module.load(assetFilePath(moduleName))
        } catch (e: Exception) {
            Log.e("X3dDataSource", "Error loading x3d module ${e.message}")
        }
        return null
    }

    override fun checkAnnotationFilesExist(): Pair<String, String>? {
        try {
            val classNameFile = File(assetFilePath("kinetics_id_to_classname.json"))
            if (!classNameFile.exists()) {
                Log.e("X3d Repository", "kinetics_id_to_classname.json does not exist")
                return null
            }
            val unicodeFile = File(assetFilePath("kinetics_classname_to_unicode.json"))
            if (!unicodeFile.exists()) {
                Log.e("X3d Repository", "kinetics_classname_to_unicode.json does not exist")
                return null
            }
            return Pair(classNameFile.absolutePath, unicodeFile.absolutePath)
        } catch (e: Exception) {
            Log.e("X3d Repository", "Error loading class names ${e.message}")
        }
        return null
    }

    override fun loadVideoMediaMetadataRetriever(videoUri: Uri): MediaMetadataRetriever? {
        try {
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, videoUri)
            if ((mediaMetadataRetriever.extractMetadata(
                    MediaMetadataRetriever.METADATA_KEY_DURATION
                )?.toLong() ?: 0) <= 0
            ) {
                Log.e("X3d Repository", "Video file is invalid")
                return null
            }
            return mediaMetadataRetriever
        } catch (e: IOException) {
            Log.e("X3d Repository", "Error loading video media metadata retriever ${e.message}")
        }
        return null
    }

    override fun extractFrameTensorsFromVideo(mediaMetadataRetriever: MediaMetadataRetriever): Tensor? {
        try {
            val videoLengthInMs = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: return null

            val inTensorBuffer = Tensor.allocateFloatBuffer(MODEL_INPUT_SIZE)
            // uniformly sample from videoTensor
//            val frameInterval = videoLengthInMs / COUNT_OF_FRAMES_PER_INFERENCE
            val frameInterval = ((SAMPLING_RATE * 1000) / FRAMES_PER_SECOND).toLong()
            for (i in 0 until COUNT_OF_FRAMES_PER_INFERENCE) {
                val frameTime = i * frameInterval
                val bitmap = mediaMetadataRetriever.getFrameAtTime(
                    frameTime,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                val resizedBitmap = bitmap?.let {
                    Bitmap.createScaledBitmap(it,
                        SIDE_SIZE,
                        SIDE_SIZE, true)
                }
                val centerCropBitmap = resizedBitmap?.let {
                    Bitmap.createBitmap(
                        it,
                        SIDE_SIZE / 2 - CROP_SIZE / 2,
                        SIDE_SIZE / 2 - CROP_SIZE / 2,
                        CROP_SIZE,
                        CROP_SIZE
                    )
                }
                TensorImageUtils.bitmapToFloatBuffer(
                    centerCropBitmap, 0, 0,
                    CROP_SIZE,
                    CROP_SIZE,
                    MEAN,
                    STD, inTensorBuffer,
                    i * NUM_CHANNELS * CROP_SIZE * CROP_SIZE
                )
            }
            return Tensor.fromBlob(
                inTensorBuffer, longArrayOf(
                    1,
                    NUM_CHANNELS.toLong(), COUNT_OF_FRAMES_PER_INFERENCE.toLong(),
                    CROP_SIZE.toLong(), CROP_SIZE.toLong()
                )
            )
        } catch (e: IOException) {
            Log.e("X3d Repository", "Error loading video tensor ${e.message}")
        }
        return null
    }

    override fun runInference(x3dModule: Module, videoTensor: Tensor): Pair<Int, Float> {
        val outputTensor = x3dModule.forward(IValue.from(videoTensor)).toTensor()
        val logits: FloatArray = outputTensor.dataAsFloatArray
        val scores = softMax(logits)
        // for debug
        val sortedScores = scores.withIndex().toSortedSet(
            compareBy<IndexedValue<Float>>({ it.value },{ it.index }).reversed()
        )
        Log.i("X3d Repository", "sortedScores: $sortedScores")
        val (maxScoreIdx, maxScore) = sortedScores.first()
        Log.i("X3d Repository", "maxScoreIdx: $maxScoreIdx, maxScore: $maxScore")
        return Pair(maxScoreIdx, maxScore)
    }

    override fun indexToEmojiInfo(
        maxScoreIdx: Int,
        classNameFilePath: String,
        classUnicodeFilePath: String
    ): Pair<String, String>? {
        // TODO: after fine-tuning, map index to emoji unicode by 19 classes
        val maxScoreClassName = JSONObject(File(classNameFilePath).readText())
            .getString(maxScoreIdx.toString()) ?: return null
//        if (maxScoreClassName == "shaking hands") { // temp. code for demo
//            return Pair(maxScoreClassName, "U+1F91D")
//        }
        val maxScoreClassUnicode = JSONObject(File(classUnicodeFilePath).readText())
            .getString(maxScoreClassName) ?: return null
        return Pair(maxScoreClassName, maxScoreClassUnicode)
    }

    override fun assetFilePath(assetName: String): String {
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                return file.absolutePath
            }
        }
    }

    private fun softMax(logits: FloatArray) : FloatArray {
        var sumExpLogits = 0.0F
        val maxLogit = logits.maxOrNull() ?: return logits
        for (i in logits.indices) {
            logits[i] = exp(logits[i] - maxLogit)
            sumExpLogits += logits[i]
        }
        for (i in logits.indices) {
            logits[i] /= sumExpLogits
        }
        return logits
    }
}