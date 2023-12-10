package com.goliath.emojihub.data_sources.local

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.goliath.emojihub.models.CreatedEmoji
import com.goliath.emojihub.models.X3dInferenceResult
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.pytorch.Module
import org.pytorch.Tensor
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class X3dDataSourceImplTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val mediaDataSourceImpl = MediaDataSourceImpl(context)
    private val x3dDataSourceImpl = X3dDataSourceImpl(context)
    // X3D TESTING UTILS
    private fun getSampleVideoUri(videoFileName: String): Uri {
        val sampleVideoAbsolutePath = x3dDataSourceImpl.assetFilePath(videoFileName)
        return Uri.fromFile(File(sampleVideoAbsolutePath))
    }
    private fun getClassFilePaths(): Pair<String, String> {
        val idToClassFileName = "Hagrid/hagrid_id_to_classname.json"
        val classToUnicodeFileName = "Hagrid/hagrid_classname_to_unicode.json"
        return x3dDataSourceImpl.checkAnnotationFilesExist(
            idToClassFileName, classToUnicodeFileName
        )!!
    }

    @Test
    fun loadModule_efficientX3dXsTutorialFloat_returnsModule() {
        // given
        val moduleName = "kinetics/efficient_x3d_xs_tutorial_float.pt"
        // when
        val module = x3dDataSourceImpl.loadModule(moduleName)
        // then
        assertTrue(module is Module)
    }

    @Test
    fun loadModule_efficientX3dsHagridFloat_returnsModule() {
        // given
        val moduleName = "Hagrid/efficient_x3d_s_hagrid_float.pt"
        // when
        val module = x3dDataSourceImpl.loadModule(moduleName)
        // then
        assertTrue(module is Module)
    }

    @Test
    fun loadModule_invalidModuleName_returnsNull() {
        // given
        val moduleName = "invalidModuleName"
        // when
        val module = x3dDataSourceImpl.loadModule(moduleName)
        // then
        assertNull(module)
    }

    @Test
    fun checkAnnotationFilesExist_kinetics400_returnsPairOfFilePaths() {
        // given
        val idToClassFileName = "kinetics/kinetics_id_to_classname.json"
        val classToUnicodeFileName = "kinetics/kinetics_classname_to_unicode.json"
        // when
        val filePaths = x3dDataSourceImpl.checkAnnotationFilesExist(
            idToClassFileName, classToUnicodeFileName
        )
        // then
        assertEquals(
            Pair("/data/user/0/com.goliath.emojihub/files/kinetics_id_to_classname.json",
                "/data/user/0/com.goliath.emojihub/files/kinetics_classname_to_unicode.json"),
            filePaths
        )
    }

    @Test
    fun checkAnnotationFilesExist_hagrid_returnsPairOfFilePaths() {
        // given
        val idToClassFileName = "Hagrid/hagrid_id_to_classname.json"
        val classToUnicodeFileName = "Hagrid/hagrid_classname_to_unicode.json"
        // when
        val filePaths = x3dDataSourceImpl.checkAnnotationFilesExist(
            idToClassFileName, classToUnicodeFileName
        )
        // then
        assertEquals(
            Pair("/data/user/0/com.goliath.emojihub/files/hagrid_id_to_classname.json",
                "/data/user/0/com.goliath.emojihub/files/hagrid_classname_to_unicode.json"),
            filePaths
        )
    }

    @Test
    fun checkAnnotationFilesExist_invalidIdToClassFileName_returnsNull() {
        // given
        val idToClassFileName = "invalidIdToClassFileName"
        val classToUnicodeFileName = "kinetics/kinetics_classname_to_unicode.json"
        // when
        val filePaths = x3dDataSourceImpl.checkAnnotationFilesExist(
            idToClassFileName, classToUnicodeFileName
        )
        // then
        assertNull(filePaths)
    }

    @Test
    fun checkAnnotationFilesExist_invalidClassToUnicodeFileName_returnsNull() {
        // given
        val idToClassFileName = "kinetics/kinetics_id_to_classname.json"
        val classToUnicodeFileName = "invalidClassToUnicodeFileName"
        // when
        val filePaths = x3dDataSourceImpl.checkAnnotationFilesExist(
            idToClassFileName, classToUnicodeFileName
        )
        // then
        assertNull(filePaths)
    }

    @Test
    fun extractFrameTensorsFromVideo_validMediaMetadataRetriever_returnsTensors() {
        // given
        val videoUri = getSampleVideoUri("Hagrid/test_palm_video.mp4")
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        // when
        val inputVideoFrameTensors = x3dDataSourceImpl
            .extractFrameTensorsFromVideo(mediaMetadataRetriever!!)
        // then
        assertEquals(
            mutableListOf(
                1,
                X3dDataSourceImpl.NUM_CHANNELS.toLong(),
                X3dDataSourceImpl.COUNT_OF_FRAMES_PER_INFERENCE.toLong(),
                X3dDataSourceImpl.CROP_SIZE.toLong(),
                X3dDataSourceImpl.CROP_SIZE.toLong()
            ),
            inputVideoFrameTensors!!.shape().toList()
        )
    }

    @Test
    fun extractFrameTensorsFromVideo_errorOnLoadingVideo_returnsNull() {
        // given
        val videoUri = getSampleVideoUri("Hagrid/test_palm_video.mp4")
        val mediaMetadataRetriever = mediaDataSourceImpl.loadVideoMediaMetadataRetriever(videoUri)
        mockkObject(mediaMetadataRetriever!!)
        every {
            mediaMetadataRetriever.getFrameAtTime(any(), any())
        } throws IOException()
        // when
        val inputVideoFrameTensors = x3dDataSourceImpl
            .extractFrameTensorsFromVideo(mediaMetadataRetriever)
        // then
        assertNull(inputVideoFrameTensors)
    }

    @Test
    fun runInference_efficientX3dXsTutorialFloat_archeryVideo_returnPredictedClassIndex5() {
        // given
        val moduleName = "kinetics/efficient_x3d_xs_tutorial_float.pt"
        val x3dModule = x3dDataSourceImpl.loadModule(moduleName)
        val inputVideoFrameTensors = Tensor.fromBlob(
            Tensor.allocateFloatBuffer(X3dDataSourceImpl.MODEL_INPUT_SIZE),
            longArrayOf(
                1,
                X3dDataSourceImpl.NUM_CHANNELS.toLong(),
                X3dDataSourceImpl.COUNT_OF_FRAMES_PER_INFERENCE.toLong(),
                X3dDataSourceImpl.CROP_SIZE.toLong(),
                X3dDataSourceImpl.CROP_SIZE.toLong()
            )
        )
        // when
        val predictedClassInfo = x3dDataSourceImpl
            .runInference(x3dModule!!, inputVideoFrameTensors!!, topK=3)
        // then
        assertEquals(3, predictedClassInfo.size)
    }

    @Test
    fun indexToCreatedEmojiList_valid3ClassNames_returnPairOfClassNameAndUnicode() {
        // given
        val (classNameFilePath, classUnicodeFilePath) = getClassFilePaths()
        val mockInferenceResults = listOf(
            X3dInferenceResult(4, 0.8f),
            X3dInferenceResult(6, 0.15f),
            X3dInferenceResult(0, 0.01f)
        )
        // when
        val createdEmojiList = x3dDataSourceImpl.indexToCreatedEmojiList(
            mockInferenceResults, classNameFilePath, classUnicodeFilePath
        )
        // then
        assertEquals(3, createdEmojiList.size)
        assertEquals(
            CreatedEmoji("like", "U+1F44D"),
            createdEmojiList[0]
        )
        assertEquals(
            CreatedEmoji("ok", "U+1F646"),
            createdEmojiList[1]
        )
        assertEquals(
            CreatedEmoji("call", "U+1F919"),
            createdEmojiList[2]
        )
    }

    @Test
    fun indexToCreatedEmojiList_withInvalidScoreIndex_returnEmptyList() {
        // given
        val (classNameFilePath, classUnicodeFilePath) = getClassFilePaths()
        val mockInferenceResults = listOf(
            X3dInferenceResult(-1, 0.8f), // invalid class index
            X3dInferenceResult(6, 0.15f),
            X3dInferenceResult(0, 0.01f)
        )
        // when
        val createdEmojiList = x3dDataSourceImpl.indexToCreatedEmojiList(
            mockInferenceResults, classNameFilePath, classUnicodeFilePath
        )
        // then
        assertEquals(0, createdEmojiList.size)
    }

    @Test
    fun assetFilePath_efficientX3dsHagridFloat_returnFilePath() {
        // given
        val moduleName = "Hagrid/efficient_x3d_s_hagrid_float.pt"
        // when
        // NOTE!: Module.load 에 absolute path 가 사용되므로 assetFilePath 는
        //       assets 폴더의 파일을 context.filesDir 에 복사해 그 파일의 absolute path 를 반환한다.
        val filePath = x3dDataSourceImpl.assetFilePath(moduleName)
        // then
        assertEquals(
            "/data/user/0/com.goliath.emojihub/files/efficient_x3d_s_hagrid_float.pt",
            filePath
        )
    }

    @Test
    fun softMax_withFloatLogitsArray_returnsSoftMaxProbabilities() {
        // given
        val logits = floatArrayOf(1.0F, 2.0F, 3.0F)
        val expectedSoftMaxProbabilities = floatArrayOf(
            0.09003057F, 0.24472848F, 0.66524094F
        )
        // when
        val softMaxProbabilities = x3dDataSourceImpl.softMax(logits)
        // then
        assertArrayEquals(expectedSoftMaxProbabilities, softMaxProbabilities, 0.0001F)
    }
}