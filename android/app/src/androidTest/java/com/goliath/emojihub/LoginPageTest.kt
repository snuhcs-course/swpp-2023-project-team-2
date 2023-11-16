package com.goliath.emojihub

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.repositories.remote.UserRepository
import com.goliath.emojihub.usecases.UserUseCase
import com.goliath.emojihub.viewmodels.UserViewModel
import com.goliath.emojihub.views.LoginPage
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

//@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class LoginPageTest {

    //@get:Rule(order = 0)
    //val hiltAndroidTestRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var userUseCase: UserUseCase

    @Inject
    lateinit var userViewModel: UserViewModel

    @Before
    fun setup() {
        //hiltAndroidTestRule.inject()

    }

    @Test
    fun loginPageUnitTest() {
        composeTestRule.setContent {
            LoginPage()
        }

        // Test Visibility of UI components
        val usernameTextField = composeTestRule.onNodeWithText("Username")
        usernameTextField.assertIsDisplayed()

        val passwordTextField = composeTestRule.onNodeWithText("Password")
        passwordTextField.assertIsDisplayed()

        val loginButton = composeTestRule.onNodeWithText("로그인")
        loginButton.assertIsDisplayed()

        // Login Button should be disabled if there is an empty textField
        loginButton.assertIsNotEnabled()

        usernameTextField.performTextInput("username7")
        loginButton.assertIsNotEnabled()

        passwordTextField.performTextInput("test")
        loginButton.assertIsEnabled()

        passwordTextField.performTextClearance()
        loginButton.assertIsNotEnabled()

        passwordTextField.performTextInput("test1")
        loginButton.assertIsEnabled()
    }

    @Test
    fun loginPageAPIUnitTest() {
        val usernameTextField = composeTestRule.onNodeWithText("Username")
        usernameTextField.assertIsDisplayed()

        val passwordTextField = composeTestRule.onNodeWithText("Password")
        passwordTextField.assertIsDisplayed()

        val loginButton = composeTestRule.onNodeWithText("로그인")
        loginButton.assertIsDisplayed()

        usernameTextField.performTextInput("username7")
        passwordTextField.performTextInput("password7")


    }
}