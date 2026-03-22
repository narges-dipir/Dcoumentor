package app.narges.documentor.domain.articles.usecase.articledetails

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.testutil.FakeArticlesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleDetailsUseCasesTest {

    @Test
    fun getArticle_forwardsArticleNumber_andReturnsRepositoryFlow() = runTest {
        val repository = FakeArticlesRepository()
        val expected = ResultState.Success.Data(
            Article(articleNumber = 1000010, articleName = "Greek Yogurt", count = 4),
        )
        repository.getArticleResult = flowOf(expected)

        val useCase = GetArticleUseCase(repository)
        val actual = useCase(1000010).first()

        assertEquals(1, repository.getArticleCallCount)
        assertEquals(1000010, repository.lastGetArticleNumber)
        assertEquals(expected, actual)
    }
}
