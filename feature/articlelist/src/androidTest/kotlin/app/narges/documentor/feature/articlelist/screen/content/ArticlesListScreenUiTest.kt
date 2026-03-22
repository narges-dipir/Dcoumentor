package app.narges.documentor.feature.articlelist.screen.content

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import app.narges.documentor.core.model.article.Article
import app.narges.documentor.feature.articlelist.state.ArticlesListUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ArticlesListScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun screenShowsArticlesAndHandlesClicks() {
        var addClicks = 0
        var loadMoreClicks = 0
        var clickedArticleNumber: Int? = null

        composeRule.setContent {
            MaterialTheme {
                ArticlesListScreen(
                    uiState = ArticlesListUiState(
                        articles = listOf(
                            Article(articleNumber = 1000001, articleName = "Red Apple", count = null),
                            Article(articleNumber = 1000002, articleName = "Orange Juice", count = 12),
                        ),
                        nextCursor = "2",
                    ),
                    onAddClicked = { addClicks++ },
                    onArticleClicked = { clickedArticleNumber = it },
                    onRetryClicked = {},
                    onLoadMoreClicked = { loadMoreClicks++ },
                )
            }
        }

        composeRule.onNodeWithText("Red Apple").performClick()
        composeRule.onNodeWithTag("article_row_1000002").performClick()
        composeRule.onNodeWithTag("add_article_button").performClick()
        composeRule.onNodeWithTag("load_more_button").performClick()

        composeRule.runOnIdle {
            assertEquals(1000002, clickedArticleNumber)
            assertEquals(1, addClicks)
            assertEquals(1, loadMoreClicks)
        }
    }

    @Test
    fun errorStateShowsRetryAndHandlesClick() {
        var retryClicks = 0

        composeRule.setContent {
            MaterialTheme {
                ArticlesListScreen(
                    uiState = ArticlesListUiState(errorMessage = "Network error"),
                    onAddClicked = {},
                    onArticleClicked = {},
                    onRetryClicked = { retryClicks++ },
                    onLoadMoreClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("retry_button").performClick()

        composeRule.runOnIdle {
            assertEquals(1, retryClicks)
        }
    }

    @Test
    fun loadingStateShowsProgressOnly() {
        composeRule.setContent {
            MaterialTheme {
                ArticlesListScreen(
                    uiState = ArticlesListUiState(isLoading = true),
                    onAddClicked = {},
                    onArticleClicked = {},
                    onRetryClicked = {},
                    onLoadMoreClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("add_article_button").assertIsDisplayed()
        composeRule.onAllNodesWithText("Load More").assertCountEquals(0)
        composeRule.onAllNodesWithText("Retry").assertCountEquals(0)
    }

    @Test
    fun loadMoreDisabledWhileLoadingMore() {
        var loadMoreClicks = 0

        composeRule.setContent {
            MaterialTheme {
                ArticlesListScreen(
                    uiState = ArticlesListUiState(
                        articles = listOf(Article(articleNumber = 1000001, articleName = "Apple", count = 1)),
                        nextCursor = "1",
                        isLoadingMore = true,
                    ),
                    onAddClicked = {},
                    onArticleClicked = {},
                    onRetryClicked = {},
                    onLoadMoreClicked = { loadMoreClicks++ },
                )
            }
        }

        composeRule.onNodeWithTag("load_more_button").assertIsNotEnabled()
        composeRule.onNodeWithTag("load_more_button").performClick()
        composeRule.runOnIdle {
            assertEquals(0, loadMoreClicks)
        }
    }

    @Test
    fun showsRedAndGreenStatusTags() {
        composeRule.setContent {
            MaterialTheme {
                ArticlesListScreen(
                    uiState = ArticlesListUiState(
                        articles = listOf(
                            Article(articleNumber = 1000001, articleName = "No Count", count = null),
                            Article(articleNumber = 1000002, articleName = "Has Count", count = 7),
                        ),
                    ),
                    onAddClicked = {},
                    onArticleClicked = {},
                    onRetryClicked = {},
                    onLoadMoreClicked = {},
                )
            }
        }

        composeRule.onNodeWithTag("article_status_1000001_red").assertIsDisplayed()
        composeRule.onNodeWithTag("article_status_1000002_green").assertIsDisplayed()
    }
}
