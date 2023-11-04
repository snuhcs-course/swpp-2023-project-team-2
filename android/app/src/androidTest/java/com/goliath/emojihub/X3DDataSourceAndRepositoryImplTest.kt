package com.goliath.emojihub

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.data_sources.local.X3dDataSourceImpl
import com.goliath.emojihub.repositories.local.X3dRepositoryImpl

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.pytorch.Module
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class X3DDataSourceAndRepositoryImplTest {
    @Test
    fun createEmoji_archeryVideo_returnPairOfClassNameAndUnicode() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        val x3dRepositoryImpl = X3dRepositoryImpl(x3DDataSourceImpl)
         /*
         shaking hands 영상에 대해서는 prediction이 정확하지 않음.
         이유!!!: inference의 시작 시점을 정확하게 잡아주는 것이 상당히 중요함.
                 sampling의 총 시간이 sampling rate (12 frames) / 30 fps = 0.4 sec 이고
                 xs expansion 기준 4 frame을 sampling 한다고 했을 때 총 1.6 sec 이므로
                 상당히 짧은 시간이다. 따라서, 시작 시점을 정확하게 잡아주는 것이 중요함.
          */
//         val sampleVideoAbsolutePath = x3dRepositoryImpl.assetFilePath("shaking hands.mp4")
        val sampleVideoAbsolutePath = x3DDataSourceImpl.assetFilePath("kinetics/archery.mp4")
        val videoUri = Uri.fromFile(File(sampleVideoAbsolutePath))

        val emojiInfo = x3dRepositoryImpl.createEmoji(videoUri)
        assert(
            // dummy emoji unicode for archery is same as the class index 5
            Pair("archery", "U+00005") == emojiInfo
        ){
            """
            Predicted class index is not 5. 
            This error may be caused by the poor performance of the model.   
            """.trimMargin()
        }
    }

    // Followings are the step by step guide to run X3dRepository unit test.
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.goliath.emojihub", appContext.packageName)
    }

    @Test
    fun assetManager_efficientX3dXsTutorialInt8_returnFileInputStream() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val assetManager = appContext.assets
        val inputStream = assetManager.open("kinetics/efficient_x3d_xs_tutorial_float.pt")
        Log.e("X3dRepositoryImplTest", "inputStream: $inputStream")
        assertNotNull(assetManager)
    }

    @Test
    fun assetFilePath_efficientX3dXsTutorialFloat_returnFilePath() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSource = X3dDataSourceImpl(appContext)
        val filePath = x3DDataSource.assetFilePath("kinetics/efficient_x3d_xs_tutorial_float.pt")
        // NOTE!: Module.load 에 absolute path 가 사용되므로 assetFilePath 는
        //       assets 폴더의 파일을 context.filesDir 에 복사해 그 파일의 absolute path 를 반환한다.
        assertEquals(
            "/data/user/0/com.goliath.emojihub/files/efficient_x3d_xs_tutorial_float.pt",
            filePath
        )
    }

    @Test
    fun loadModule_efficientX3dXsTutorialFloat_returnModule() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        val module = x3DDataSourceImpl.loadModule("kinetics/efficient_x3d_xs_tutorial_float.pt")
        assertTrue(module is Module)
    }

    @Test
    fun checkAnnotationFilesExist_kinetics400_returnPairOfFilePaths() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        val filePaths = x3DDataSourceImpl.checkAnnotationFilesExist()
        assertEquals(
            Pair("/data/user/0/com.goliath.emojihub/files/kinetics_id_to_classname.json",
                "/data/user/0/com.goliath.emojihub/files/kinetics_classname_to_unicode.json"),
            filePaths
        )
    }

    //  how can I access to the video file in the device?
    //  -> route this issue by using file in assets folder
    @Test
    fun loadVideoMediaMetadataRetriever_videoUri_returnMediaMetadataRetriever() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        val sampleVideoAbsolutePath = x3DDataSourceImpl.assetFilePath("kinetics/archery.mp4")
        val videoUri = Uri.fromFile(File(sampleVideoAbsolutePath))

        val mediaMetadataRetriever = x3DDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        assertTrue(
            (mediaMetadataRetriever?.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0) > 0
        )
    }

    @Test
    fun extractFrameTensorsFromVideo_mediaMetadataRetriever_returnTensors() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        val sampleVideoAbsolutePath = x3DDataSourceImpl.assetFilePath("kinetics/archery.mp4")
        val videoUri = Uri.fromFile(File(sampleVideoAbsolutePath))

        val mediaMetadataRetriever = x3DDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        if (mediaMetadataRetriever == null){
            Log.e("X3dRepositoryImplTest", "mediaMetadataRetriever is null")
            return
        }
        // Target method: extractFrameTensorsFromVideo
        val startTime = System.currentTimeMillis()
        val inputVideoFrameTensors = x3DDataSourceImpl.extractFrameTensorsFromVideo(mediaMetadataRetriever)
        val elapsedTime = System.currentTimeMillis() - startTime
        Log.i("X3dRepositoryImplTest", "elapsedTime: $elapsedTime ms")
        if (inputVideoFrameTensors == null){
            Log.e("X3dRepositoryImplTest", "tensors is null")
            return
        }
        assertEquals(
            mutableListOf(
                1,
                X3dDataSourceImpl.NUM_CHANNELS.toLong(),
                X3dDataSourceImpl.COUNT_OF_FRAMES_PER_INFERENCE.toLong(),
                X3dDataSourceImpl.CROP_SIZE.toLong(),
                X3dDataSourceImpl.CROP_SIZE.toLong()
            ),
            inputVideoFrameTensors.shape().toList()
        )
    }

    @Test
    fun runInference_efficientX3dXsTutorialFloat_archeryVideo_returnPredictedClassIndex5() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        // load x3d Module
        val x3dModule = x3DDataSourceImpl.loadModule("kinetics/efficient_x3d_xs_tutorial_float.pt")
        if (x3dModule == null){
            Log.e("X3dRepositoryImplTest", "x3dModule is null")
            return
        }
        // load archery video input tensors
        val sampleVideoAbsolutePath = x3DDataSourceImpl.assetFilePath("kinetics/archery.mp4")
        val videoUri = Uri.fromFile(File(sampleVideoAbsolutePath))
        val mediaMetadataRetriever = x3DDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        if (mediaMetadataRetriever == null){
            Log.e("X3dRepositoryImplTest", "mediaMetadataRetriever is null")
            return
        }
        val inputVideoFrameTensors = x3DDataSourceImpl.extractFrameTensorsFromVideo(mediaMetadataRetriever)
        if (inputVideoFrameTensors == null){
            Log.e("X3dRepositoryImplTest", "tensors is null")
            return
        }
        // run inference
        val startTime = System.currentTimeMillis()
        val predictedClassInfo = x3DDataSourceImpl.runInference(x3dModule, inputVideoFrameTensors)
        val elapsedTime = System.currentTimeMillis() - startTime
        Log.i("X3dRepositoryImplTest", "elapsedTime: $elapsedTime ms")

        assert (predictedClassInfo.second > X3dRepositoryImpl.SCORE_THRESHOLD) {
            """
            X3dRepositoryImplTest, Score of ${predictedClassInfo.second} is lower than 
            threshold ${X3dRepositoryImpl.SCORE_THRESHOLD}
            """.trimMargin()
        }
        assert(5 == predictedClassInfo.first) {
            """
            Predicted class index is not 5. 
            This error may be caused by the poor performance of the model.   
            """.trimMargin()
        }
    }

    @Test
    fun indexToEmojiInfo_0_returnPairOfClassNameAndUnicode() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3DDataSourceImpl = X3dDataSourceImpl(appContext)
        val filePaths = x3DDataSourceImpl.checkAnnotationFilesExist()
        if (filePaths == null){
            Log.e("X3dRepositoryImplTest", "checkAnnotationFilesExist() returns null")
            return
        }
        val classNameFilePath = filePaths.first
        val classUnicodeFilePath = filePaths.second
        val emojiInfo = x3DDataSourceImpl.indexToEmojiInfo(0, classNameFilePath, classUnicodeFilePath)
        assertEquals(
            // dummy emoji unicode is same as the class index
            Pair("abseiling", "U+00000"),
            emojiInfo
        )
    }
}