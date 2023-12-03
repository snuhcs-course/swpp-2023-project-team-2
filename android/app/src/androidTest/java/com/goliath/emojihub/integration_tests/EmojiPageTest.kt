import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EmojiPageTest {

    @get:Rule
    private val composeTestRule = createComposeRule()

    @Test
    fun usernameField_IsDisplayed() {
        composeTestRule.onNodeWithTag("usernameField")
            .assertIsDisplayed()
    }
}