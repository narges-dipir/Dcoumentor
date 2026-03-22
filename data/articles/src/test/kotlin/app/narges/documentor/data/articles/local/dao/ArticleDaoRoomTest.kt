package app.narges.documentor.data.articles.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.narges.documentor.data.articles.local.db.ArticlesDatabase
import app.narges.documentor.data.articles.testutil.articleEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ArticleDaoRoomTest {
    private lateinit var database: ArticlesDatabase
    private lateinit var dao: ArticleDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, ArticlesDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.articleDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun getByOffset_returnsOrderedSlice() = runTest {
        dao.upsertAll(
            listOf(
                articleEntity(articleNumber = 1000003, articleName = "Three"),
                articleEntity(articleNumber = 1000001, articleName = "One"),
                articleEntity(articleNumber = 1000002, articleName = "Two"),
            ),
        )

        val page = dao.getByOffset(limit = 2, offset = 1)

        assertEquals(listOf(1000002, 1000003), page.map { it.articleNumber })
    }

    @Test
    fun getByNumber_upsertAndClearAll_workAsExpected() = runTest {
        dao.upsert(articleEntity(articleNumber = 1000022, articleName = "Find Me", count = null))

        val stored = dao.getByNumber(1000022)
        assertEquals("Find Me", stored?.articleName)
        assertEquals(1, dao.count())

        dao.clearAll()

        assertNull(dao.getByNumber(1000022))
        assertEquals(0, dao.count())
    }
}
