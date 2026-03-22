package app.narges.documentor.data.articles.fake

import app.narges.documentor.data.articles.model.CreateArticleRequest
import app.narges.documentor.data.articles.model.UpdateArticleRequest
import com.google.gson.GsonBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FakeArticleBackendTest {
    private val gson = GsonBuilder().create()

    @Test
    fun `getArticles returns cursor paginated result`() {
        val backend = FakeArticleBackend(seedJson = seedJson(), gson = gson)

        val page = backend.getArticles(cursor = "2", limit = 2)

        assertEquals("2", page.cursor)
        assertEquals("4", page.nextCursor)
        assertEquals(2, page.limit)
        assertEquals(2, page.items.size)
        assertEquals(1000003, page.items[0].articleNumber)
        assertEquals(1000004, page.items[1].articleNumber)
    }

    @Test
    fun `createArticle adds new article to list`() {
        val backend = FakeArticleBackend(seedJson = seedJson(), gson = gson)

        val created = backend.createArticle(
            CreateArticleRequest(
                articleNumber = 1000006,
                articleName = "Fresh Milk",
                count = null,
            ),
        )

        assertEquals(1000006, created.articleNumber)
        assertEquals("Fresh Milk", created.articleName)
        assertNull(created.count)

        val fullList = backend.getArticles(cursor = null, limit = 20)
        assertEquals(6, fullList.items.size)
        assertNull(fullList.nextCursor)
    }

    @Test
    fun `updateArticle changes name and count`() {
        val backend = FakeArticleBackend(seedJson = seedJson(), gson = gson)

        val updated = backend.updateArticle(
            articleNumber = 1000002,
            request = UpdateArticleRequest(
                articleName = "Orange Juice Premium",
                count = 77,
            ),
        )

        assertEquals("Orange Juice Premium", updated.articleName)
        assertEquals(77, updated.count)

        val loaded = backend.getArticle(1000002)
        assertEquals("Orange Juice Premium", loaded.articleName)
        assertEquals(77, loaded.count)
    }

    private fun seedJson(): String {
        return """
            {
              "articles": [
                {"articleNumber": 1000001, "articleName": "Red Apple", "count": 10},
                {"articleNumber": 1000002, "articleName": "Orange Juice", "count": null},
                {"articleNumber": 1000003, "articleName": "Whole Wheat Bread", "count": 5},
                {"articleNumber": 1000004, "articleName": "Greek Yogurt", "count": null},
                {"articleNumber": 1000005, "articleName": "Roasted Coffee Beans", "count": 2}
              ]
            }
        """.trimIndent()
    }
}
