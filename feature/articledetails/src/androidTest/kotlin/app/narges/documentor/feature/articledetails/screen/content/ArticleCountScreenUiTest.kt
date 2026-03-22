package app.narges.documentor.feature.articledetails.screen.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import app.narges.documentor.feature.articledetails.state.ArticleCountUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ArticleCountScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun redArticleStartsEmptyAndSaveWorks() {
        var saveClicks = 0

        composeRule.setContent {
            var uiState by mutableStateOf(
                ArticleCountUiState(
                    articleNumber = 1000002,
                    articleName = "Orange Juice",
                    countInput = "",
                ),
            )
            MaterialTheme {
                ArticleCountScreen(
                    uiState = uiState,
                    onCountChanged = { uiState = uiState.copy(countInput = it) },
                    onSaveClicked = { saveClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Orange Juice (1000002)").assertExists()
        composeRule.onNodeWithTag("article_count_input").assertTextEquals("")

        composeRule.onNodeWithTag("article_count_input").performTextInput("77")
        composeRule.onNodeWithTag("article_count_input").assertTextEquals("77")

        composeRule.onNodeWithTag("article_count_save_button").performClick()

        composeRule.runOnIdle {
            assertEquals(1, saveClicks)
        }
    }

    @Test
    fun loadingStateDisablesInputAndSave() {
        composeRule.setContent {
            MaterialTheme {
                ArticleCountScreen(
                    uiState = ArticleCountUiState(
                        articleNumber = 1000003,
                        articleName = "Bread",
                        isLoading = true,
                    ),
                    onCountChanged = {},
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("article_count_input").assertIsNotEnabled()
        composeRule.onNodeWithTag("article_count_save_button").assertIsNotEnabled()
    }

    @Test
    fun showsValidationAndServerErrors() {
        composeRule.setContent {
            MaterialTheme {
                ArticleCountScreen(
                    uiState = ArticleCountUiState(
                        articleNumber = 1000003,
                        articleName = "Bread",
                        countError = "Count must be between 0 and 999",
                        errorMessage = "Server failed",
                    ),
                    onCountChanged = {},
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithText("Count must be between 0 and 999").assertIsDisplayed()
        composeRule.onNodeWithText("Server failed").assertIsDisplayed()
    }

    @Test
    fun headerHidesWhenArticleNameIsBlank() {
        composeRule.setContent {
            MaterialTheme {
                ArticleCountScreen(
                    uiState = ArticleCountUiState(
                        articleNumber = 1000003,
                        articleName = "",
                    ),
                    onCountChanged = {},
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithText("Count Article").assertIsDisplayed()
        composeRule.onAllNodesWithText("(1000003)").assertCountEquals(0)
    }

    @Test
    fun imeDoneActionHandled() {
        composeRule.setContent {
            var uiState by mutableStateOf(
                ArticleCountUiState(articleNumber = 1000002, articleName = "Orange Juice"),
            )
            MaterialTheme {
                ArticleCountScreen(
                    uiState = uiState,
                    onCountChanged = { uiState = uiState.copy(countInput = it) },
                    onSaveClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("article_count_input").performTextInput("88")
        composeRule.onNodeWithTag("article_count_input").performImeAction()
        composeRule.onNodeWithTag("article_count_input").assertTextEquals("88")
    }
}
