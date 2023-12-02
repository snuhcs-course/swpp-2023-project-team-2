import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.RootActivity
import com.goliath.emojihub.data_sources.CustomError
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginPageIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<RootActivity>()

    @Test
    fun usernameField_IsDisplayed() {
        composeTestRule.onNodeWithTag("UsernameField")
            .assertIsDisplayed()
    }

    @Test
    fun passwordField_IsDisplayed() {
        composeTestRule.onNodeWithTag("PasswordField")
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_IsDisplayed() {
        composeTestRule.onNodeWithTag("LoginButton")
            .assertIsDisplayed()
    }

    @Test
    fun registerButton_IsDisplayed() {
        composeTestRule.onNodeWithTag("RegisterButton")
            .assertIsDisplayed()
    }

    @Test
    fun guestModeButton_IsDisplayed() {
        composeTestRule.onNodeWithTag("GuestModeButton")
            .assertIsDisplayed()
    }


    fun loginButton_Clicked_WithEmptyFields_ShowsError() {
        TODO("NOT IMPLEMENTED YET")
        composeTestRule.onNodeWithTag("LoginButton")
            .performClick()

        // Assuming an error message is shown when fields are empty
        onView(withText("Please fill in all fields"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loginButton_Clicked_WithInvalidUsername_ShowsNotFoundError(){
        // If username is invalid, password input doesn't matter
        composeTestRule.onNodeWithTag("UsernameField")
            .performTextInput("invalidUsername")
        composeTestRule.onNodeWithTag("LoginButton")
            .performClick()
        // Wait until the error message is shown
        composeTestRule.waitForIdle()
        // Assuming an error message is shown when username is invalid
        composeTestRule.onNodeWithText(CustomError.NOT_FOUND.body(), useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_Clicked_WithInvalidPassword_ShowsUnauthorizedError(){
        composeTestRule.onNodeWithTag("UsernameField")
            .performTextInput("username7")
        composeTestRule.onNodeWithTag("PasswordField")
            .performTextInput("invalidPassword")
        composeTestRule.onNodeWithTag("LoginButton")
            .performClick()
        // Wait until the error message is shown
        composeTestRule.waitForIdle()
        // Assuming an error message is shown when password is invalid
        composeTestRule.onNodeWithText(CustomError.UNAUTHORIZED.body(), useUnmergedTree = true)
            .assertIsDisplayed()
    }

    @Test
    fun loginButton_Clicked_WithValidCredentials_ShowsFeedPage(){
        composeTestRule.onNodeWithTag("UsernameField")
            .performTextInput("username7")
        composeTestRule.onNodeWithTag("PasswordField")
            .performTextInput("password7")
        composeTestRule.onNodeWithTag("LoginButton")
            .performClick()
        // Wait until the feed page is shown
        composeTestRule.waitForIdle()
        // Assuming the homepage is shown when credentials are valid
        composeTestRule.onNodeWithText("Feed")
            .assertIsDisplayed()
    }

    @Test
    fun registerButton_Clicked_ShowsSignUpPage(){
        composeTestRule.onNodeWithTag("RegisterButton")
            .performClick()
        // Wait until the sign up page is shown
        composeTestRule.waitForIdle()
        // Assuming the sign up page is shown when register button is clicked
        composeTestRule.onNodeWithTag("SignUpPage")
            .assertIsDisplayed()
    }

    // @Test
    // TODO: Not implemented yet
    fun guestModeButton_Clicked_ShowsFeedPage(){
        composeTestRule.onNodeWithTag("GuestModeButton")
            .performClick()
        // Wait until the feed page is shown
        composeTestRule.waitForIdle()
        // Assuming the homepage is shown when guest mode button is clicked
        composeTestRule.onNodeWithTag("FeedPage")
            .assertIsDisplayed()
    }
}