package com.goliath.emojihub.repositories.local

import android.graphics.BitmapFactory
import android.util.Log
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

interface X3dRepository {
    fun createEmoji(videoUrl: String) : String
    fun loadModel() : Module?
    fun loadVideoTensor(videoUrl: String) : Tensor
    fun sampleFrameTensor(videoTensor: Tensor) : Tensor
    fun runInference(x3dModule: Module, videoTensor: Tensor) : Int
    fun indexToEmojiUnicode(maxScoreIdx: Int) : String
}

@Singleton
class X3dRepositoryImpl @Inject constructor(

): X3dRepository {
    override fun createEmoji(videoUrl: String): String {
        val x3dModule = loadModel()
        val videoTensor = loadVideoTensor(videoUrl)
        val maxScoreIdx = runInference(x3dModule, videoTensor)
        val emojiUnicode = indexToEmojiUnicode(maxScoreIdx)
        return emojiUnicode
    }

    override fun loadModel() : Module? {
        var x3dModule: Nothing? = null
        try {
            var x3dModule = Module.load("assets/efficient_x3d_s_int8.pt")
        } catch (e: IOException) {
            Log.e("X3d Repository", "Error loading x3d module", e)
            e.printStackTrace()
        }
        return x3dModule
    }

    override fun loadVideoTensor(videoUrl: String) : Tensor {
        // local video path to inputstream
        val videoInputStream = FileInputStream(videoUrl)
        val videoBitmap = BitmapFactory.decodeStream(videoInputStream)
        // FIXME: tensor shape should be (B, C, T, H, W)
        val videoTensor = TensorImageUtils.bitmapToFloat32Tensor(
            videoBitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        return videoTensor
    }

    override fun sampleFrameTensor(videoTensor: Tensor) : Tensor {
        // TODO: sample frame tensor from video tensor
    }

    override fun runInference(x3dModule: Module, videoTensor: Tensor) : Int {
        val outputTensor = x3dModule.forward(IValue.from(videoTensor)).toTensor()
        val scores: FloatArray = outputTensor.dataAsFloatArray
        val maxScoreIdx = scores.indices.maxByOrNull { scores[it] } ?: -1
        return maxScoreIdx
    }

    override fun indexToEmojiUnicode(maxScoreIdx: Int) : String {
        // TODO: after finetuning, map index to emoji unicode by 19 classes
        val handShakeUnicode : String = "U+1F91D"
        return handShakeUnicode
    }
}