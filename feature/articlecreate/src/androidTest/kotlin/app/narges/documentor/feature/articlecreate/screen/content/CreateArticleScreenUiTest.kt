package app.narges.documentor.feature.articlecreate.screen.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import app.narges.documentor.feature.articlecreate.state.CreateArticleUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CreateArticleScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun inputsAndSaveWork() {
        var saveClicks = 0

        composeRule.setContent {
            var uiState by mutableStateOf(CreateArticleUiState())
            MaterialTheme {
                CreateArticleScreen(
                    uiState = uiState,
                    onArticleNameChanged = { uiState = uiState.copy(articleName = it) },
                    onArticleNumberChanged = { uiState = uiState.copy(articleNumberInput = it) },
                    onSaveClicked = { saveClicks++ },
                )
            }
        }

        composeRule.onNodeWithTag("create_article_name_input").performTextInput("Milk")
        composeRule.onNodeWithTag("create_article_number_input").performTextInput("1234567")

        composeRule.onNodeWithTag("create_article_name_input").assertTextEquals("Milk")
        composeRule.onNodeWithTag("create_article_number_input").assertTextEquals("1234567")

        composeRule.onNodeWithTag("create_article_save_button").performClick()

        composeRule.runOnIdle {
            assertEquals(1, saveClicks)
        }
    }

    @Test
    fun savingStateDisablesInputsAndButton() {
        composeRule.setContent {
            MaterialTheme {
                CreateArticleScreen(
                    uiState = CreateArticleUiState(
                        articleName = "Milk",
                        articleNumberInput = "1234567",
                        isSaving = true,
                    ),
                    onArticleNameChanged = {},
                    onArticleNumberChanged = {},
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("create_article_name_input").assertIsNotEnabled()
        composeRule.onNodeWithTag("create_article_number_input").assertIsNotEnabled()
        composeRule.onNodeWithTag("create_article_save_button").assertIsNotEnabled()
    }

    @Test
    fun validationAndServerErrorsAreVisible() {
        composeRule.setContent {
            MaterialTheme {
                CreateArticleScreen(
                    uiState = CreateArticleUiState(
                        articleNameError = "Name too short",
                        articleNumberError = "Must be 7 digits",
                        errorMessage = "Duplicate article",
                    ),
                    onArticleNameChanged = {},
                    onArticleNumberChanged = {},
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithText("Name too short").assertIsDisplayed()
        composeRule.onNodeWithText("Must be 7 digits").assertIsDisplayed()
        composeRule.onNodeWithText("Duplicate article").assertIsDisplayed()
    }

    @Test
    fun imeActionsAreHandled() {
        composeRule.setContent {
            var uiState by mutableStateOf(CreateArticleUiState())
            MaterialTheme {
                CreateArticleScreen(
                    uiState = uiState,
                    onArticleNameChanged = { uiState = uiState.copy(articleName = it) },
                    onArticleNumberChanged = { uiState = uiState.copy(articleNumberInput = it) },
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("create_article_name_input").performTextInput("Milk")
        composeRule.onNodeWithTag("create_article_name_input").performImeAction()
        composeRule.onNodeWithTag("create_article_number_input").performTextInput("1234567")
        composeRule.onNodeWithTag("create_article_number_input").performImeAction()

        composeRule.onNodeWithTag("create_article_name_input").assertTextEquals("Milk")
        composeRule.onNodeWithTag("create_article_number_input").assertTextEquals("1234567")
    }
}
