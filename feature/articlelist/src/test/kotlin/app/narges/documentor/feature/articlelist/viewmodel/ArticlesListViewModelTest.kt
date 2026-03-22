package app.narges.documentor.feature.articlelist.viewmodel

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ErrorState
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import app.narges.documentor.domain.articles.usecase.articlelist.GetArticlesUseCase
import app.narges.documentor.feature.articlelist.rule.MainDispatcherRule
import app.narges.documentor.feature.articlelist.state.contract.ArticlesListEvent
import app.narges.documentor.feature.articlelist.state.contract.ArticlesListIntent
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ArticlesListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_loadsFirstPageAndUpdatesState() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            getArticlesProvider = { cursor, _ ->
                if (cursor == null) {
                    flowOf(
                        ResultState.Loading,
                        ResultState.Success.Data(
                            ArticleCursorPage(
                                cursor = null,
                                nextCursor = "1",
                                limit = 20,
                                items = listOf(sampleArticle(1000001, "Apple", 10)),
                            ),
                        ),
                    )
                } else {
                    flowOf(ResultState.Success.Empty)
                }
            }
        }

        val viewModel = ArticlesListViewModel(GetArticlesUseCase(repository))

        assertEquals(listOf(null), repository.requestedCursors)
        assertEquals(listOf(20), repository.requestedLimits)
        assertEquals(1, viewModel.state.value.articles.size)
        assertEquals("1", viewModel.state.value.nextCursor)
        assertEquals(null, viewModel.state.value.errorMessage)
    }

    @Test
    fun onAddClicked_emitsNavigateToCreateEvent() = runBlocking {
        val repository = FakeArticlesRepository()
        val viewModel = ArticlesListViewModel(GetArticlesUseCase(repository))
        val eventDeferred = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) { viewModel.events.first() }
        }

        viewModel.onIntent(ArticlesListIntent.OnAddClicked)

        assertTrue(eventDeferred.await() is ArticlesListEvent.NavigateToCreateArticle)
    }

    @Test
    fun onArticleClicked_emitsNavigateToCountEvent() = runBlocking {
        val repository = FakeArticlesRepository()
        val viewModel = ArticlesListViewModel(GetArticlesUseCase(repository))
        val eventDeferred = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) { viewModel.events.first() }
        }

        viewModel.onIntent(ArticlesListIntent.OnArticleClicked(articleNumber = 1000011))

        val event = eventDeferred.await() as ArticlesListEvent.NavigateToArticleCount
        assertEquals(1000011, event.articleNumber)
    }

    @Test
    fun onLoadMoreClicked_appendsNextPageItems() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            getArticlesProvider = { cursor, _ ->
                when (cursor) {
                    null -> flowOf(
                        ResultState.Loading,
                        ResultState.Success.Data(
                            ArticleCursorPage(
                                cursor = null,
                                nextCursor = "2",
                                limit = 20,
                                items = listOf(sampleArticle(1000001, "Apple", 10)),
                            ),
                        ),
                    )

                    "2" -> flowOf(
                        ResultState.Loading,
                        ResultState.Success.Data(
                            ArticleCursorPage(
                                cursor = "2",
                                nextCursor = null,
                                limit = 20,
                                items = listOf(sampleArticle(1000002, "Bread", null)),
                            ),
                        ),
                    )

                    else -> flowOf(ResultState.Success.Empty)
                }
            }
        }

        val viewModel = ArticlesListViewModel(GetArticlesUseCase(repository))

        viewModel.onIntent(ArticlesListIntent.OnLoadMoreClicked)

        assertEquals(listOf(null, "2"), repository.requestedCursors)
        assertEquals(listOf(1000001, 1000002), viewModel.state.value.articles.map { it.articleNumber })
        assertEquals(null, viewModel.state.value.nextCursor)
    }

    @Test
    fun errorResult_updatesErrorMessage() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            getArticlesProvider = { _, _ ->
                flowOf(
                    ResultState.Loading,
                    ResultState.Error(type = ErrorState.NetworkUnavailable, message = "offline"),
                )
            }
        }

        val viewModel = ArticlesListViewModel(GetArticlesUseCase(repository))

        assertEquals("offline", viewModel.state.value.errorMessage)
        assertTrue(viewModel.state.value.articles.isEmpty())
    }

    private class FakeArticlesRepository : ArticlesRepository {
        var requestedCursors: MutableList<String?> = mutableListOf()
        var requestedLimits: MutableList<Int> = mutableListOf()

        var getArticlesProvider: (String?, Int) -> Flow<ResultState<ArticleCursorPage>> = { _, _ ->
            flowOf(ResultState.Success.Empty)
        }

        override fun getArticles(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>> {
            requestedCursors += cursor
            requestedLimits += limit
            return getArticlesProvider(cursor, limit)
        }

        override fun getArticle(articleNumber: Int): Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)

        override fun createArticle(
            articleNumber: Int,
            articleName: String,
            count: Int?,
        ): Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)

        override fun updateArticle(
            articleNumber: Int,
            articleName: String?,
            count: Int?,
        ): Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)
    }

    private fun sampleArticle(number: Int, name: String, count: Int?): Article = Article(
        articleNumber = number,
        articleName = name,
        count = count,
    )
}
