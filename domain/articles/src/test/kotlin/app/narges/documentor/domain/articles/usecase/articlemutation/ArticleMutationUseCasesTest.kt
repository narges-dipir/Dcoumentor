package app.narges.documentor.domain.articles.usecase.articlemutation

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.testutil.FakeArticlesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleMutationUseCasesTest {

    @Test
    fun createArticle_forwardsPayload_andReturnsRepositoryFlow() = runTest {
        val repository = FakeArticlesRepository()
        val expected = ResultState.Success.Data(
            Article(articleNumber = 1000020, articleName = "Created", count = 7),
        )
        repository.createArticleResult = flowOf(expected)

        val useCase = CreateArticleUseCase(repository)
        val actual = useCase(articleNumber = 1000020, articleName = "Created", count = 7).first()

        assertEquals(1000020, repository.lastCreateArticleNumber)
        assertEquals("Created", repository.lastCreateArticleName)
        assertEquals(7, repository.lastCreateCount)
        assertEquals(expected, actual)
    }

    @Test
    fun updateArticle_forwardsPayload_andReturnsRepositoryFlow() = runTest {
        val repository = FakeArticlesRepository()
        val expected = ResultState.Success.Data(
            Article(articleNumber = 1000020, articleName = "Updated", count = 9),
        )
        repository.updateArticleResult = flowOf(expected)

        val useCase = UpdateArticleUseCase(repository)
        val actual = useCase(articleNumber = 1000020, articleName = "Updated", count = 9).first()

        assertEquals(1000020, repository.lastUpdateArticleNumber)
        assertEquals("Updated", repository.lastUpdateArticleName)
        assertEquals(9, repository.lastUpdateCount)
        assertEquals(expected, actual)
    }
}
