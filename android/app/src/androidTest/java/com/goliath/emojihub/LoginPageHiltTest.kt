package com.goliath.emojihub

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import com.goliath.emojihub.repositories.remote.FakeUserRepository
import com.goliath.emojihub.repositories.remote.FakeUserRepositoryImpl
import com.goliath.emojihub.viewmodels.UserViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class LoginPageHiltTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeRule = createComposeRule()

    lateinit var userViewModel: UserViewModel

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun loginPageHiltTest() {
        composeRule.setContent {
            userViewModel = hiltViewModel()
        }
        assert(1 + 2 == 3)
    }
}