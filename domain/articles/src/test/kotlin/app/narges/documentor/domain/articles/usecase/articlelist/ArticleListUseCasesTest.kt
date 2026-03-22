package app.narges.documentor.domain.articles.usecase.articlelist

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.testutil.FakeArticlesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleListUseCasesTest {

    @Test
    fun getArticles_forwardsCursorLimit_andReturnsRepositoryFlow() = runTest {
        val repository = FakeArticlesRepository()
        val expected = ResultState.Success.Data(
            ArticleCursorPage(
                cursor = null,
                nextCursor = "2",
                limit = 2,
                items = listOf(
                    Article(articleNumber = 1000001, articleName = "Red Apple", count = 10),
                    Article(articleNumber = 1000002, articleName = "Orange Juice", count = null),
                ),
            ),
        )
        repository.getArticlesResult = flowOf(expected)

        val useCase = GetArticlesUseCase(repository)
        val actual = useCase(cursor = null, limit = 2).first()

        assertEquals(1, repository.getArticlesCallCount)
        assertEquals(null, repository.lastCursor)
        assertEquals(2, repository.lastLimit)
        assertEquals(expected, actual)
    }
}
