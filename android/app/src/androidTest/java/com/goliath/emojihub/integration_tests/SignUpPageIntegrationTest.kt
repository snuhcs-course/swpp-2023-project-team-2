import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goliath.emojihub.views.SignUpPage
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpPageIntegrationTest {

    @get:Rule
    private val composeTestRule = createComposeRule()
    @Before
    fun setUp() {
        composeTestRule.setContent { SignUpPage() }
    }

    @Test
    fun emailField_IsDisplayed() {
        composeTestRule.onNodeWithTag("emailField")
            .assertIsDisplayed()
    }

    @Test
    fun usernameField_IsDisplayed() {
        composeTestRule.onNodeWithTag("usernameField")
            .assertIsDisplayed()
    }

    @Test
    fun passwordField_IsDisplayed() {
        composeTestRule.onNodeWithTag("passwordField")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_IsDisplayed() {
        composeTestRule.onNodeWithTag("signUpButton")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_Clicked_WithEmptyEmail_ShowsEmptyError() {
        TODO("NOT IMPLEMENTED YET")
        composeTestRule.onNodeWithTag("signUpButton")
            .performClick()

        // Assuming an error message is shown when email is empty
        composeTestRule.onNodeWithText("이메일을 입력해주세요.")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_Clicked_WithEmptyUsername_ShowsEmptyError() {
        TODO("NOT IMPLEMENTED YET")
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("validEmail@gmail.com")
        composeTestRule.onNodeWithTag("signUpButton")
            .performClick()

        // Assuming an error message is shown when username is empty
        composeTestRule.onNodeWithText("아이디를 입력해주세요.")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_Clicked_WithEmptyPassword_ShowsEmptyError() {
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("validEmail@gmail.com")
        composeTestRule.onNodeWithTag("usernameField")
            .performTextInput("validUsername")
        composeTestRule.onNodeWithTag("signUpButton")
            .performClick()

        // Assuming an error message is shown when password is empty
        composeTestRule.onNodeWithText("비밀번호를 입력해주세요.")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_Clicked_WithInvalidEmailForm_ShowsInvalidError() {
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("invalidEmail")
        composeTestRule.onNodeWithTag("usernameField")
            .performTextInput("validUsername")
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("validPassword")
        composeTestRule.onNodeWithTag("signUpButton")
            .performClick()

        // Assuming an error message is shown when email is invalid
        composeTestRule.onNodeWithText("이메일 형식이 올바르지 않습니다.")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_Clicked_WithExistingUsername_ShowsExistingError() {
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("validEmail@gmail.com")
        composeTestRule.onNodeWithTag("usernameField")
            .performTextInput("username7")
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("validPassword")
        composeTestRule.onNodeWithTag("signUpButton")
            .performClick()

        // Assuming the username is already taken
        composeTestRule.onNodeWithText("이미 있는 계정입니다.")
            .assertIsDisplayed()
    }

    @Test
    fun signUpButton_Clicked_WithValidInputs_ShowsSuccessMessageAndNavigateToFeedPage() {
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("validEmail@gamil.com")
        composeTestRule.onNodeWithTag("usernameField")
            .performTextInput("validUsername")
        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("validPassword")
        composeTestRule.onNodeWithTag("signUpButton")
            .performClick()

        // Assuming the sign up is successful
        composeTestRule.onNodeWithText("회원가입이 완료되었습니다.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithTag("feedPage")
            .assertIsDisplayed()
    }
}
