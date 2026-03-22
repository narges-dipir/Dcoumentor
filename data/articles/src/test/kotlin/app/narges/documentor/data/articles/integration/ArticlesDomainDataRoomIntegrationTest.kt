package app.narges.documentor.data.articles.integration

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.data.articles.local.db.ArticlesDatabase
import app.narges.documentor.data.articles.local.source.ArticlesLocalDataSource
import app.narges.documentor.data.articles.repository.ArticlesRepositoryImpl
import app.narges.documentor.data.articles.testutil.FakeArticlesApi
import app.narges.documentor.data.articles.testutil.articleDto
import app.narges.documentor.data.articles.testutil.articlePageResponse
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArticlesDomainDataRoomIntegrationTest {
    private lateinit var database: ArticlesDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ArticlesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getArticles_persistsRemoteToRoom_andSubsequentReadServesCache() = runTest {
        val api = FakeArticlesApi(
            pageResponse = articlePageResponse(
                items = listOf(
                    articleDto(articleNumber = 1000001, articleName = "Remote Apple"),
                    articleDto(articleNumber = 1000002, articleName = "Remote Bread"),
                ),
                nextCursor = null,
            ),
        )
        val repository = ArticlesRepositoryImpl(
            remoteApi = api,
            localDataSource = ArticlesLocalDataSource(database.articleDao()),
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val firstFetch = repository.getArticles(cursor = null, limit = 20).drop(1).first()
        assertTrue(firstFetch is ResultState.Success.Data)
        assertEquals(1, api.getArticlesCalls)

        api.getArticlesFailure = IllegalStateException("remote should not be called when cache exists")
        val secondFetch = repository.getArticles(cursor = null, limit = 20).drop(1).first()

        assertTrue(secondFetch is ResultState.Success.Data)
        val page = (secondFetch as ResultState.Success.Data).value
        assertEquals(listOf(1000001, 1000002), page.items.map { it.articleNumber })
        assertEquals(1, api.getArticlesCalls)
    }

    @Test
    fun getArticle_persistsRemoteToRoom_andLaterReturnsCachedArticle() = runTest {
        val api = FakeArticlesApi(
            articleByNumber = mutableMapOf(
                1000099 to articleDto(articleNumber = 1000099, articleName = "Remote Single", count = 11),
            ),
        )
        val repository = ArticlesRepositoryImpl(
            remoteApi = api,
            localDataSource = ArticlesLocalDataSource(database.articleDao()),
            ioDispatcher = StandardTestDispatcher(testScheduler),
        )

        val first = repository.getArticle(1000099).drop(1).first()
        assertTrue(first is ResultState.Success.Data)
        assertEquals(1, api.getArticleCalls)

        api.getArticleFailure = IllegalStateException("remote should not be called when cache exists")
        val second = repository.getArticle(1000099).drop(1).first()

        assertTrue(second is ResultState.Success.Data)
        val article = (second as ResultState.Success.Data).value
        assertEquals("Remote Single", article.articleName)
        assertEquals(1, api.getArticleCalls)
    }
}
