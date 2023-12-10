package com.goliath.emojihub.integration_tests

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.RootActivity
import com.goliath.emojihub.ui.theme.EmojiHubTheme
import com.goliath.emojihub.views.EmojiPage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
class EmojiPageTest {
    // FIXME: RootActivity should be reset to the EmojiPage after each test

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun emojiPage_isDisplayed() {
        composeTestRule.onNodeWithText("Emoji")
            .assertIsDisplayed()
    }

    @Test
    fun emojiCell_isDisplayed() {
        composeTestRule.onNodeWithText("@", useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun emojiCell_isClickable() {
        composeTestRule.onNodeWithText("@", useUnmergedTree = true)
            .assertHasClickAction()
    }

    @Test
    fun addEmojiButton_isDisplayed() {
        composeTestRule.onNodeWithContentDescription("Emoji")
            .assertIsDisplayed()
    }
}