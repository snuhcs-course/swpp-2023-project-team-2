package com.goliath.emojihub

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.repositories.local.X3dRepositoryImpl

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.pytorch.Module

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

    @Test
    fun loadClassNames_kinetics400_returnClassNamesHashMap() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val x3dRepositoryImpl = X3dRepositoryImpl(appContext)
        val classNames = x3dRepositoryImpl.loadClassNames()
        assertTrue(classNames is HashMap<Int, String>)
    }
}