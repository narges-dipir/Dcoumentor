package app.narges.documentor.feature.articlecreate.viewmodel

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ErrorState
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import app.narges.documentor.domain.articles.usecase.articlemutation.CreateArticleUseCase
import app.narges.documentor.feature.articlecreate.rule.MainDispatcherRule
import app.narges.documentor.feature.articlecreate.state.contract.CreateArticleEvent
import app.narges.documentor.feature.articlecreate.state.contract.CreateArticleIntent
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CreateArticleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun onFieldChanged_updatesStateAndSanitizesNumber() = runBlocking {
        val repository = FakeArticlesRepository()
        val viewModel = CreateArticleViewModel(CreateArticleUseCase(repository))

        viewModel.onIntent(CreateArticleIntent.OnArticleNameChanged("  Orange Juice  "))
        viewModel.onIntent(CreateArticleIntent.OnArticleNumberChanged("10a00b01"))

        assertEquals("  Orange Juice  ", viewModel.state.value.articleName)
        assertEquals("100001", viewModel.state.value.articleNumberInput)
    }

    @Test
    fun onSaveClicked_withInvalidInputs_setsValidationErrors() = runBlocking {
        val repository = FakeArticlesRepository()
        val viewModel = CreateArticleViewModel(CreateArticleUseCase(repository))

        viewModel.onIntent(CreateArticleIntent.OnArticleNameChanged("ab"))
        viewModel.onIntent(CreateArticleIntent.OnArticleNumberChanged("123"))
        viewModel.onIntent(CreateArticleIntent.OnSaveClicked)

        assertEquals("Name must be at least 3 characters", viewModel.state.value.articleNameError)
        assertEquals("Article number must be 7 digits", viewModel.state.value.articleNumberError)
        assertEquals(0, repository.createCalls)
    }

    @Test
    fun onSaveClicked_withValidInput_callsCreateAndEmitsNavigateBack() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            createArticleResult = flowOf(
                ResultState.Loading,
                ResultState.Success.Data(Article(articleNumber = 1000001, articleName = "Apple", count = null)),
            )
        }
        val viewModel = CreateArticleViewModel(CreateArticleUseCase(repository))
        val eventDeferred = async(start = CoroutineStart.UNDISPATCHED) {
            withTimeout(1_000) { viewModel.events.first() }
        }

        viewModel.onIntent(CreateArticleIntent.OnArticleNameChanged("Apple"))
        viewModel.onIntent(CreateArticleIntent.OnArticleNumberChanged("1000001"))
        viewModel.onIntent(CreateArticleIntent.OnSaveClicked)

        assertEquals(1, repository.createCalls)
        assertEquals(1000001, repository.lastCreateNumber)
        assertEquals("Apple", repository.lastCreateName)
        assertNull(repository.lastCreateCount)
        assertTrue(eventDeferred.await() is CreateArticleEvent.NavigateBack)
    }

    @Test
    fun onSaveClicked_whenRepositoryError_updatesErrorState() = runBlocking {
        val repository = FakeArticlesRepository().apply {
            createArticleResult = flowOf(
                ResultState.Loading,
                ResultState.Error(type = ErrorState.Validation, message = "duplicate article"),
            )
        }
        val viewModel = CreateArticleViewModel(CreateArticleUseCase(repository))

        viewModel.onIntent(CreateArticleIntent.OnArticleNameChanged("Apple"))
        viewModel.onIntent(CreateArticleIntent.OnArticleNumberChanged("1000001"))
        viewModel.onIntent(CreateArticleIntent.OnSaveClicked)

        assertEquals("duplicate article", viewModel.state.value.errorMessage)
        assertEquals(false, viewModel.state.value.isSaving)
    }

    private class FakeArticlesRepository : ArticlesRepository {
        var createArticleResult: Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)
        var createCalls: Int = 0
        var lastCreateNumber: Int? = null
        var lastCreateName: String? = null
        var lastCreateCount: Int? = null

        override fun getArticles(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>> =
            flowOf(ResultState.Success.Empty)

        override fun getArticle(articleNumber: Int): Flow<ResultState<Article>> =
            flowOf(ResultState.Success.Empty)

        override fun syncArticles(limit: Int): Flow<ResultState<Unit>> =
            flowOf(ResultState.Success.Data(Unit))

        override fun createArticle(
            articleNumber: Int,
            articleName: String,
            count: Int?,
        ): Flow<ResultState<Article>> {
            createCalls += 1
            lastCreateNumber = articleNumber
            lastCreateName = articleName
            lastCreateCount = count
            return createArticleResult
        }

        override fun updateArticle(
            articleNumber: Int,
            articleName: String?,
            count: Int?,
        ): Flow<ResultState<Article>> =
            flowOf(ResultState.Success.Empty)
    }
}
