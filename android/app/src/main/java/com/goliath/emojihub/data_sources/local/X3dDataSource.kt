package com.goliath.emojihub.data_sources.local

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.X3dInferenceResult
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
    fun checkAnnotationFilesExist(
        idToClassFileName: String, classToUnicodeFileName: String
    ): Pair<String, String>?
    fun extractFrameTensorsFromVideo(mediaMetadataRetriever: MediaMetadataRetriever): Tensor?
    fun runInference(x3dModule: Module, videoTensor: Tensor, topK: Int): List<X3dInferenceResult>
    fun indexToCreatedEmojiList(
        inferenceResults: List<X3dInferenceResult>,
        classNameFilePath: String,
        classUnicodeFilePath: String
    ): List<CreatedEmoji>
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
        private const val SAMPLING_RATE = 6
        const val COUNT_OF_FRAMES_PER_INFERENCE = 13
        const val MODEL_INPUT_SIZE = COUNT_OF_FRAMES_PER_INFERENCE * NUM_CHANNELS * CROP_SIZE * CROP_SIZE
    }

    override fun loadModule(moduleName: String): Module? {
        try {
            return Module.load(assetFilePath(moduleName))
        } catch (e: Exception) {
            Log.e("X3dDataSource", "Error loading x3d module ${e.message}")
        }
        return null
    }

    override fun checkAnnotationFilesExist(
        idToClassFileName: String, classToUnicodeFileName: String
    ): Pair<String, String>? {
        try {
            val classNameFile = File(assetFilePath(idToClassFileName))
            if (!classNameFile.exists()) {
                Log.e("X3dDataSource", "$idToClassFileName does not exist")
                return null
            }
            val unicodeFile = File(assetFilePath(classToUnicodeFileName))
            if (!unicodeFile.exists()) {
                Log.e("X3dDataSource", "$classToUnicodeFileName does not exist")
                return null
            }
            return Pair(classNameFile.absolutePath, unicodeFile.absolutePath)
        } catch (e: Exception) {
            Log.e("X3dDataSource", "Error loading class names ${e.message}")
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
            // val frameInterval = videoLengthInMs / COUNT_OF_FRAMES_PER_INFERENCE
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
            Log.e("X3dDataSource", "Error loading video tensor ${e.message}")
        }
        return null
    }

    override fun runInference(
        x3dModule: Module,
        videoTensor: Tensor,
        topK: Int
    ): List<X3dInferenceResult> {
        val outputTensor = x3dModule.forward(IValue.from(videoTensor)).toTensor()
        val logits: FloatArray = outputTensor.dataAsFloatArray
        val scores = softMax(logits)

        val sortedScores = scores.withIndex().toSortedSet(
            compareBy<IndexedValue<Float>>({ it.value },{ it.index }).reversed()
        )
        val topKScores = sortedScores.take(topK)
        Log.i("X3dDataSource", "topKScores: $topKScores")
        return topKScores.map { X3dInferenceResult(it.index, it.value) }
    }

    override fun indexToCreatedEmojiList(
        inferenceResults: List<X3dInferenceResult>,
        classNameFilePath: String,
        classUnicodeFilePath: String
    ): List<CreatedEmoji> {
        val classNameJSONObject = JSONObject(File(classNameFilePath).readText())
        val classUnicodeJSONObject = JSONObject(File(classUnicodeFilePath).readText())

        val createdEmojiList = mutableListOf<CreatedEmoji>()
        return try {
            for (result in inferenceResults) {
                val className = classNameJSONObject.getString(result.scoreIdx.toString())
                val classUnicode = classUnicodeJSONObject.getString(className)
                createdEmojiList.add(CreatedEmoji(className, classUnicode))
            }
            createdEmojiList
        } catch (e: Exception) {
            Log.e("X3dDataSource", "Error loading class name or unicode ${e.message}")
            emptyList()
        }
    }

    fun assetFilePath(assetName: String): String {
        val file = File(context.filesDir, assetName.split("/").last())
        // FIXME: assetFilePath로 호출하고자 하는 파일에 변경사항(개발자 관점)이 생길 시 반영할 수 없음
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

    fun softMax(logits: FloatArray) : FloatArray {
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