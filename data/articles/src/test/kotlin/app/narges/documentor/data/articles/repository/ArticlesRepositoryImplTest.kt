package app.narges.documentor.data.articles.repository

import app.narges.documentor.core.result.ResultState
import app.narges.documentor.data.articles.local.dao.ArticleDao
import app.narges.documentor.data.articles.local.entity.ArticleEntity
import app.narges.documentor.data.articles.local.source.ArticlesLocalDataSource
import app.narges.documentor.data.articles.testutil.FakeArticlesApi
import app.narges.documentor.data.articles.testutil.articleDto
import app.narges.documentor.data.articles.testutil.articleEntity
import app.narges.documentor.data.articles.testutil.articlePageResponse
import com.google.gson.JsonSyntaxException
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticlesRepositoryImplTest {

    @Test
    fun `getArticles returns cached data when cache exists`() = runTest {
        val dao = FakeArticleDao().apply {
            upsert(articleEntity(articleNumber = 1000001, articleName = "Cached Apple", count = 5))
        }
        val api = FakeArticlesApi()
        val repository = ArticlesRepositoryImpl(
            remoteApi = api,
            localDataSource = ArticlesLocalDataSource(dao),
        )

        val states = repository.getArticles(cursor = null, limit = 20).take(2).toList()

        assertTrue(states[0] is ResultState.Loading)
        assertTrue(states[1] is ResultState.Success.Data)
        val payload = (states[1] as ResultState.Success.Data).value
        assertEquals(1, payload.items.size)
        assertEquals("Cached Apple", payload.items.first().articleName)
        assertEquals(0, api.getArticlesCalls)
    }

    @Test
    fun `getArticles fetches remote and caches when cache is empty`() = runTest {
        val dao = FakeArticleDao()
        val api = FakeArticlesApi(
            pageResponse = articlePageResponse(
                items = listOf(
                    articleDto(1000001, "Remote Apple", 3),
                    articleDto(1000002, "Remote Bread", null),
                ),
            ),
        )
        val repository = ArticlesRepositoryImpl(
            remoteApi = api,
            localDataSource = ArticlesLocalDataSource(dao),
        )

        val states = repository.getArticles(cursor = null, limit = 20).take(2).toList()

        assertTrue(states[1] is ResultState.Success.Data)
        assertEquals(1, api.getArticlesCalls)
        val cached = dao.getByOffset(limit = 20, offset = 0)
        assertEquals(2, cached.size)
        assertEquals("Remote Apple", cached.first().articleName)
    }

    @Test
    fun `getArticles retries on retryable error and then succeeds`() = runTest {
        val dao = FakeArticleDao()
        val api = FakeArticlesApi(
            pageResponse = articlePageResponse(
                items = listOf(articleDto(1000003, "Retry Milk", 1)),
            ),
        ).apply {
            getArticlesFailuresRemaining = 2
            getArticlesFailure = IOException("temporary network error")
        }
        val repository = ArticlesRepositoryImpl(
            remoteApi = api,
            localDataSource = ArticlesLocalDataSource(dao),
        )

        val states = repository.getArticles(cursor = null, limit = 20).take(2).toList()

        assertTrue(states[1] is ResultState.Success.Data)
        assertEquals(3, api.getArticlesCalls)
    }

    @Test
    fun `getArticles does not retry on non-retryable error`() = runTest {
        val dao = FakeArticleDao()
        val api = FakeArticlesApi().apply {
            getArticlesFailure = JsonSyntaxException("bad payload")
        }
        val repository = ArticlesRepositoryImpl(
            remoteApi = api,
            localDataSource = ArticlesLocalDataSource(dao),
        )

        val states = repository.getArticles(cursor = null, limit = 20).take(2).toList()

        assertTrue(states[0] is ResultState.Loading)
        assertTrue(states[1] is ResultState.Error)
        assertEquals(1, api.getArticlesCalls)
    }
}

private class FakeArticleDao : ArticleDao {
    private val store = linkedMapOf<Int, ArticleEntity>()

    override suspend fun getByOffset(limit: Int, offset: Int): List<ArticleEntity> {
        return store.values.drop(offset).take(limit)
    }

    override suspend fun getByNumber(articleNumber: Int): ArticleEntity? = store[articleNumber]

    override suspend fun upsert(article: ArticleEntity) {
        store[article.articleNumber] = article
    }

    override suspend fun upsertAll(articles: List<ArticleEntity>) {
        articles.forEach { store[it.articleNumber] = it }
    }

    override suspend fun count(): Int = store.size

    override suspend fun clearAll() {
        store.clear()
    }
}
