package app.narges.documentor.feature.articledetails.viewmodel

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ErrorState
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import app.narges.documentor.domain.articles.usecase.articledetails.GetArticleUseCase
import app.narges.documentor.domain.articles.usecase.articlemutation.UpdateArticleUseCase
import app.narges.documentor.feature.articledetails.rule.MainDispatcherRule
import app.narges.documentor.feature.articledetails.state.contract.ArticleCountEvent
import app.narges.documentor.feature.articledetails.state.contract.ArticleCountIntent
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

class ArticleCountViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun init_loadsArticleAndPrefillsState() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            getArticleResult = flowOf(
                ResultState.Loading,
                ResultState.Success.Data(
                    Article(articleNumber = 1000003, articleName = "Bread", count = 14),
                ),
            )
        }

        val viewModel = ArticleCountViewModel(
            getArticleUseCase = GetArticleUseCase(repository),
            updateArticleUseCase = UpdateArticleUseCase(repository),
        )
        viewModel.onIntent(ArticleCountIntent.OnScreenEntered(articleNumber = 1000003))

        assertEquals(1000003, repository.lastGetArticleNumber)
        assertEquals("Bread", viewModel.state.value.articleName)
        assertEquals("14", viewModel.state.value.countInput)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun onCountChanged_filtersNonDigits() = runBlocking {
        val repository = FakeArticlesRepository()
        val viewModel = ArticleCountViewModel(
            getArticleUseCase = GetArticleUseCase(repository),
            updateArticleUseCase = UpdateArticleUseCase(repository),
        )
        viewModel.onIntent(ArticleCountIntent.OnScreenEntered(articleNumber = 1000003))

        viewModel.onIntent(ArticleCountIntent.OnCountChanged("12a3"))

        assertEquals("123", viewModel.state.value.countInput)
    }

    @Test
    fun onSaveClicked_withInvalidCount_setsValidationError() = runBlocking {
        val repository = FakeArticlesRepository()
        val viewModel = ArticleCountViewModel(
            getArticleUseCase = GetArticleUseCase(repository),
            updateArticleUseCase = UpdateArticleUseCase(repository),
        )
        viewModel.onIntent(ArticleCountIntent.OnScreenEntered(articleNumber = 1000003))

        viewModel.onIntent(ArticleCountIntent.OnCountChanged("1000"))
        viewModel.onIntent(ArticleCountIntent.OnSaveClicked)

        assertEquals("Count must be between 0 and 999", viewModel.state.value.countError)
        assertEquals(0, repository.updateCalls)
    }

    @Test
    fun onSaveClicked_withValidCount_callsUpdateAndNavigatesBack() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            updateArticleResult = flowOf(
                ResultState.Loading,
                ResultState.Success.Data(
                    Article(articleNumber = 1000003, articleName = "Bread", count = 22),
                ),
            )
        }

        val viewModel = ArticleCountViewModel(
            getArticleUseCase = GetArticleUseCase(repository),
            updateArticleUseCase = UpdateArticleUseCase(repository),
        )
        viewModel.onIntent(ArticleCountIntent.OnScreenEntered(articleNumber = 1000003))
        val eventDeferred = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) { viewModel.events.first() }
        }

        viewModel.onIntent(ArticleCountIntent.OnCountChanged("22"))
        viewModel.onIntent(ArticleCountIntent.OnSaveClicked)

        assertEquals(1, repository.updateCalls)
        assertEquals(1000003, repository.lastUpdateNumber)
        assertEquals(22, repository.lastUpdateCount)
        assertTrue(eventDeferred.await() is ArticleCountEvent.NavigateBack)
    }

    @Test
    fun onSaveClicked_whenRepositoryError_setsErrorMessage() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            updateArticleResult = flowOf(
                ResultState.Loading,
                ResultState.Error(type = ErrorState.Validation, message = "count rejected"),
            )
        }

        val viewModel = ArticleCountViewModel(
            getArticleUseCase = GetArticleUseCase(repository),
            updateArticleUseCase = UpdateArticleUseCase(repository),
        )
        viewModel.onIntent(ArticleCountIntent.OnScreenEntered(articleNumber = 1000003))

        viewModel.onIntent(ArticleCountIntent.OnCountChanged("22"))
        viewModel.onIntent(ArticleCountIntent.OnSaveClicked)

        assertEquals("count rejected", viewModel.state.value.errorMessage)
        assertEquals(false, viewModel.state.value.isSaving)
    }

    private class FakeArticlesRepository : ArticlesRepository {
        var getArticleResult: Flow<ResultState<Article>> = flowOf(
            ResultState.Success.Data(
                Article(articleNumber = 1000003, articleName = "Bread", count = null),
            ),
        )
        var updateArticleResult: Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)

        var lastGetArticleNumber: Int? = null
        var updateCalls: Int = 0
        var lastUpdateNumber: Int? = null
        var lastUpdateCount: Int? = null

        override fun getArticles(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>> =
            flowOf(ResultState.Success.Empty)

        override fun getArticle(articleNumber: Int): Flow<ResultState<Article>> {
            lastGetArticleNumber = articleNumber
            return getArticleResult
        }

        override fun syncArticles(limit: Int): Flow<ResultState<Unit>> =
            flowOf(ResultState.Success.Data(Unit))

        override fun createArticle(
            articleNumber: Int,
            articleName: String,
            count: Int?,
        ): Flow<ResultState<Article>> =
            flowOf(ResultState.Success.Empty)

        override fun updateArticle(
            articleNumber: Int,
            articleName: String?,
            count: Int?,
        ): Flow<ResultState<Article>> {
            updateCalls += 1
            lastUpdateNumber = articleNumber
            lastUpdateCount = count
            return updateArticleResult
        }
    }
}
