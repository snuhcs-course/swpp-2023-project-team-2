import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.models.createDummyEmoji
import com.goliath.emojihub.views.EmojiPage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmojiPageIntegrationTest {

    @get:Rule
    private val composeTestRule = createComposeRule()
    @Before
    fun setUp() {
        val emojiList = (1..10).map { createDummyEmoji() }
        composeTestRule.setContent { EmojiPage(emojiList) }
    }

    @Test
    fun usernameField_IsDisplayed() {
        composeTestRule.onNodeWithTag("usernameField")
            .assertIsDisplayed()
    }
}