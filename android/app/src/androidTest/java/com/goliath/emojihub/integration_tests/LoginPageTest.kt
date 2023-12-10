package com.goliath.emojihub.integration_tests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.RootActivity
import com.goliath.emojihub.data_sources.CustomError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalTestApi::class)
class LoginPageTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()
    // FIXME: RootActivity should be reset after each test

    @Test
    fun usernameField_IsDisplayed() {
        composeTestRule.onNodeWithText("Username")
            .assertIsDisplayed()
    }

    @Test
    fun passwordField_IsDisplayed() {
        composeTestRule.onNodeWithText("Password")
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_IsDisplayed() {
        composeTestRule.onNodeWithText("로그인")
            .assertIsDisplayed()
    }

    @Test
    fun registerButton_IsDisplayed() {
        composeTestRule.onNodeWithText("계정 생성")
            .assertIsDisplayed()
    }

    @Test
    fun guestModeButton_IsDisplayed() {
        composeTestRule.onNodeWithText("비회원 모드로 시작하기")
            .assertIsDisplayed()
    }


    fun loginButton_Clicked_WithEmptyFields_ShowsError() {
        composeTestRule.onNodeWithText("로그인")
            .assertHasNoClickAction()
    }

    @Test
    fun loginButton_Clicked_WithInvalidUsername_ShowsNotFoundError(){
        // If username is invalid, password input doesn't matter
        composeTestRule.onNodeWithText("Username")
            .performTextInput("invalidUsername")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password7")
        composeTestRule.onNodeWithText("로그인")
            .performClick()
        // Wait until the error message is shown
        composeTestRule.waitUntilExactlyOneExists(hasText("에러"))
        // Assuming an error message is shown when username is invalid
        composeTestRule.onNodeWithText(CustomError.NOT_FOUND.body(), useUnmergedTree = true)
            .assertIsDisplayed()
        // reset the error message
        composeTestRule.onNodeWithText("확인")
            .performClick()
    }

    @Test
    fun loginButton_Clicked_WithInvalidPassword_ShowsUnauthorizedError(){
        composeTestRule.onNodeWithText("Username")
            .performTextInput("username7")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("invalidPassword")
        composeTestRule.onNodeWithText("로그인")
            .performClick()
        // Wait until the error message is shown
        // FIXME: this wait command is not stable, not actually it meant to be
        // FIXME: another wait command may be needed
        composeTestRule.waitUntilExactlyOneExists(hasText("에러"))
        // Assuming an error message is shown when password is invalid
        composeTestRule.onNodeWithText(CustomError.UNAUTHORIZED.body(),
            useUnmergedTree = true, substring = true, ignoreCase = true)
            .assertIsDisplayed()
//        // reset error state
//        composeTestRule.onNodeWithText("확인")
//            .performClick()
//        composeTestRule.waitForIdle()
//        composeTestRule.onNodeWithText("Username")
//            .performTextClearance()
//        composeTestRule.onNodeWithText("Password")
//            .performTextClearance()
    }

    // @Test
    // FIXME: RootActivity should be reset after each test
    fun loginButton_Clicked_WithValidCredentials_ShowsFeedPage(){
        composeTestRule.onNodeWithText("Username")
            .performTextInput("username7")
        composeTestRule.onNodeWithText("Password")
            .performTextInput("password7")
        composeTestRule.onNodeWithText("로그인")
            .performClick()
        // Wait until the feed page is shown
        // FIXME: this wait command is not stable, not actually it meant to be
        composeTestRule.waitUntilAtLeastOneExists(hasText("Feed"))
        // Assuming the homepage is shown when credentials are valid
        composeTestRule.onNodeWithText("Feed")
            .assertIsDisplayed()
        // reset the authentication
        composeTestRule.activity.applicationContext.getSharedPreferences("EMOJI_HUB", 0)
            .edit().putString("accessToken", null).apply()
        composeTestRule.waitUntilExactlyOneExists(hasText("로그인"))
    }

//    @Test
    fun registerButton_Clicked_ShowsSignUpPage(){
        composeTestRule.onNodeWithTag("RegisterButton")
            .performClick()
        // Wait until the sign up page is shown
        // FIXME: this wait command is not stable, not actually it meant to be
        composeTestRule.waitUntilExactlyOneExists(hasText("계정 생성"))
        // Assuming the sign up page is shown when register button is clicked
        composeTestRule.onNodeWithText("계정 생성")
            .assertIsDisplayed()
    }

//     @Test
    // TODO: Not implemented yet
    fun guestModeButton_Clicked_ShowsFeedPage(){
        composeTestRule.onNodeWithText("비회원")
            .performClick()
        // Wait until the feed page is shown
        composeTestRule.waitUntilAtLeastOneExists(hasText("Feed"))
        // Assuming the homepage is shown when guest mode button is clicked
        composeTestRule.onNodeWithText("Feed")
            .assertIsDisplayed()
    }
}