package com.goliath.emojihub.repositories.local

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

interface X3dRepository {
    fun createEmoji(videoUri: Uri): Pair<String, String>?
}

@Singleton
class X3dRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): X3dRepository {
    companion object{
        // !IMPORTANT! : CROP_SIZE, COUNT_OF_FRAMES_PER_INFERENCE should be same as
        //              those specified when converting Pytorch model to TorchScript
        //              In tutorial jupyter notebook (pytorchvideo/tutorials/accelerator/Use_PytorchVideo_Accelerator_Model_Zoo.ipynb),
        //              CROP_SIZE = 160, COUNT_OF_FRAMES_PER_INFERENCE = 4
        const val SIDE_SIZE = 160
        val MEAN = floatArrayOf(0.45F, 0.45F, 0.45F)
        val STD = floatArrayOf(0.225F, 0.225F, 0.225F)
        const val CROP_SIZE = 160
        const val NUM_CHANNELS = 3
        const val FRAMES_PER_SECOND = 30
        const val COUNT_OF_FRAMES_PER_INFERENCE = 4
        const val MODEL_INPUT_SIZE = COUNT_OF_FRAMES_PER_INFERENCE * NUM_CHANNELS * CROP_SIZE * CROP_SIZE
        const val SCORE_THRESHOLD = 0.5F
    }

    override fun createEmoji(videoUri: Uri): Pair<String, String>? {
        val x3dModule = loadModule("efficient_x3d_xs_tutorial_int8.pt") ?: return null
        val (classNameFilePath, classUnicodeFilePath) = checkAnnotationFilesExist() ?: return null
        val mediaMetadataRetriever = loadVideoMediaMetadataRetriever(videoUri) ?: return null
        val videoTensor = extractFrameTensorsFromVideo(mediaMetadataRetriever) ?: return null
        val (maxScoreIdx, maxScore) = runInference(x3dModule, videoTensor)?: return null
        if (maxScore < SCORE_THRESHOLD) {
            Log.e("X3d Repository", "Score is lower than threshold")
            return null
        }
        val (maxScoreClassName, maxScoreClassUnicode) = indexToEmojiInfo(
            maxScoreIdx, classNameFilePath, classUnicodeFilePath
        )?: return null
        return Pair(maxScoreClassName, maxScoreClassUnicode)
    }


    fun loadModule(moduleName: String): Module? {
        try {
            return Module.load(assetFilePath(moduleName))
        } catch (e: IOException) {
            Log.e("X3d Repository", "Error loading x3d module", e)
            e.printStackTrace()
        }
        return null
    }

    @Deprecated("loadClassNames() is deprecated.")
    fun loadClassNames(): HashMap<Int, String>? {
        try {
            val classNames = HashMap<Int, String>()
            val jsonText = context.assets.open("kinetics_id_to_classname.json")
                                        .bufferedReader().readText()
            val jsonObject = JSONObject(jsonText)
            val keysItr = jsonObject.keys()
            while (keysItr.hasNext()) {
                val key = keysItr.next()
                classNames[key.toInt()] = jsonObject.getString(key)
            }
            return classNames
        } catch (e: IOException) {
            Log.e("X3d Repository", "Error loading class names", e)
            e.printStackTrace()
        }
        return null
    }

    fun checkAnnotationFilesExist(): Pair<String, String>? {
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
        } catch (e: IOException) {
            Log.e("X3d Repository", "Error loading class names", e)
            e.printStackTrace()
        }
        return null
    }

    fun loadVideoMediaMetadataRetriever(videoUri: Uri): MediaMetadataRetriever? {
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
            Log.e("X3d Repository", "Error loading video media metadata retriever", e)
            e.printStackTrace()
        }
        return null
    }

    fun extractFrameTensorsFromVideo(mediaMetadataRetriever: MediaMetadataRetriever): Tensor? {
        try {
            val videoLengthInMs = mediaMetadataRetriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0

            val inTensorBuffer = Tensor.allocateFloatBuffer(MODEL_INPUT_SIZE)
            // uniformly sample from videoTensor
            val frameInterval = videoLengthInMs / COUNT_OF_FRAMES_PER_INFERENCE
            for (i in 0 until COUNT_OF_FRAMES_PER_INFERENCE) {
                val frameTime = i * frameInterval
                val bitmap = mediaMetadataRetriever.getFrameAtTime(
                    frameTime * 1000,
                    MediaMetadataRetriever.OPTION_CLOSEST_SYNC
                )
                val resizedBitmap = bitmap?.let {
                    Bitmap.createScaledBitmap(it, SIDE_SIZE, SIDE_SIZE, true)
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
                    centerCropBitmap, 0, 0, CROP_SIZE, CROP_SIZE,
                    MEAN, STD, inTensorBuffer,
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
            Log.e("X3d Repository", "Error loading video tensor", e)
            e.printStackTrace()
        }
        return null
    }

    fun runInference(
        x3dModule: Module,
        videoTensor: Tensor
    ): Pair<Int, Float>? {
        val outputTensor = x3dModule.forward(IValue.from(videoTensor)).toTensor()
        val scores: FloatArray = outputTensor.dataAsFloatArray
        // for debug
        val sortedScores = scores.withIndex().toSortedSet(compareByDescending { it.value })
        Log.i("X3d Repository", "sortedScores: $sortedScores")
        val (maxScoreIdx, maxScore) = scores.withIndex().maxByOrNull { it.value }?: return null
        return Pair(maxScoreIdx, maxScore)
    }

    fun indexToEmojiInfo(
        maxScoreIdx: Int,
        classNameFilePath: String,
        classUnicodeFilePath: String
    ): Pair<String, String>? {
        // TODO: after fine-tuning, map index to emoji unicode by 19 classes
        val maxScoreClassName = JSONObject(File(classNameFilePath).readText())
                                    .getString(maxScoreIdx.toString()) ?: return null
        if (maxScoreClassName == "shaking hands") { // temp. code for demo
            return Pair(maxScoreClassName, "U+1F91D")
        }
        val maxScoreClassUnicode = JSONObject(File(classUnicodeFilePath).readText())
                                    .getString(maxScoreClassName) ?: return null
        return Pair(maxScoreClassName, maxScoreClassUnicode)
    }

    fun assetFilePath(assetName: String): String {
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
}
