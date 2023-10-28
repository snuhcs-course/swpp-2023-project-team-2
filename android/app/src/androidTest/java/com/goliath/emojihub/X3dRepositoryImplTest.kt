package com.goliath.emojihub

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.repositories.local.X3dRepositoryImpl

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.pytorch.Module
import java.io.File
import java.util.Arrays

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class X3dRepositoryImplTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.goliath.emojihub", appContext.packageName)
    }

    @Test
    fun assetManager_efficient_x3d_xs_tutorial_int8_pt_returnFileInputStream() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val assetManager = appContext.assets
        val inputStream = assetManager.open("efficient_x3d_xs_tutorial_int8.pt")
        Log.e("X3dRepositoryImplTest", "inputStream: $inputStream")
        assertNotNull(assetManager)
    }

    @Test
    fun assetFilePath_efficient_x3d_xs_int8_pt_returnFilePath() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val filePath = x3dRepositoryImpl.assetFilePath("efficient_x3d_xs_tutorial_int8.pt")
        // NOTE!: Module.load 에 absolute path 가 사용되므로 assetFilePath 는
        //       assets 폴더의 파일을 context.filesDir 에 복사해 그 파일의 absolute path 를 반환한다.
        assertEquals(
            "/data/user/0/com.goliath.emojihub/files/efficient_x3d_xs_tutorial_int8.pt",
            filePath
        )
    }

    @Test
    fun loadModule_x3d_returnModule() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val module = x3dRepositoryImpl.loadModule()
        assertTrue(module is Module)
    }

    @Deprecated("loadClassNames() is deprecated.")
    fun loadClassNames_kinetics400_returnClassNamesHashMap() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val classNames = x3dRepositoryImpl.loadClassNames()
        assertTrue(classNames is HashMap<Int, String>)
    }

    @Test
    fun checkAnnotationFilesExist_kinetics400_returnPairOfFilePaths() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val filePaths = x3dRepositoryImpl.checkAnnotationFilesExist()
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
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val sampleVideoAbsolutePath = x3dRepositoryImpl.assetFilePath("archery.mp4")
        val videoUri = Uri.fromFile(File(sampleVideoAbsolutePath))

        val mediaMetadataRetriever = x3dRepositoryImpl.loadVideoMediaMetadataRetriever(videoUri)
        assertTrue(
            (mediaMetadataRetriever?.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLong() ?: 0) > 0
        )
    }

    @Test
    fun extractFrameTensorsFromVideo_mediaMetadataRetriever_returnTensors() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val sampleVideoAbsolutePath = x3dRepositoryImpl.assetFilePath("archery.mp4")
        val videoUri = Uri.fromFile(File(sampleVideoAbsolutePath))

        val mediaMetadataRetriever = x3dRepositoryImpl.loadVideoMediaMetadataRetriever(videoUri)
        if (mediaMetadataRetriever == null){
            Log.e("X3dRepositoryImplTest", "mediaMetadataRetriever is null")
            return
        }
        val tensors = x3dRepositoryImpl.extractFrameTensorsFromVideo(mediaMetadataRetriever)
        if (tensors == null){
            Log.e("X3dRepositoryImplTest", "tensors is null")
            return
        }
        assertEquals(
            mutableListOf(
                1,
                X3dRepositoryImpl.NUM_CHANNELS.toLong(),
                X3dRepositoryImpl.COUNT_OF_FRAMES_PER_INFERENCE.toLong(),
                X3dRepositoryImpl.CROP_SIZE.toLong(),
                X3dRepositoryImpl.CROP_SIZE.toLong()
            ),
            tensors.shape().toList()
        )
    }

    @Test
    fun indexToEmojiInfo_0_returnPairOfClassNameAndUnicode() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val filePaths = x3dRepositoryImpl.checkAnnotationFilesExist()
        if (filePaths == null){
            Log.e("X3dRepositoryImplTest", "checkAnnotationFilesExist() returns null")
            return
        }
        val classNameFilePath = filePaths.first
        val classUnicodeFilePath = filePaths.second
        val emojiInfo = x3dRepositoryImpl.indexToEmojiInfo(0, classNameFilePath, classUnicodeFilePath!!)
        assertEquals(
            // dummy emoji unicode
            Pair("abseiling", "U+00000"),
            emojiInfo
        )
    }

}