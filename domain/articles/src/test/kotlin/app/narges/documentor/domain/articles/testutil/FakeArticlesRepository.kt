package app.narges.documentor.domain.articles.testutil

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal class FakeArticlesRepository : ArticlesRepository {
    var getArticlesResult: Flow<ResultState<ArticleCursorPage>> = flowOf(ResultState.Success.Empty)
    var getArticleResult: Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)
    var createArticleResult: Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)
    var updateArticleResult: Flow<ResultState<Article>> = flowOf(ResultState.Success.Empty)

    var lastCursor: String? = null
    var lastLimit: Int? = null
    var lastGetArticleNumber: Int? = null

    var lastCreateArticleNumber: Int? = null
    var lastCreateArticleName: String? = null
    var lastCreateCount: Int? = null

    var lastUpdateArticleNumber: Int? = null
    var lastUpdateArticleName: String? = null
    var lastUpdateCount: Int? = null

    var getArticlesCallCount: Int = 0
    var getArticleCallCount: Int = 0

    override fun getArticles(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>> {
        getArticlesCallCount += 1
        lastCursor = cursor
        lastLimit = limit
        return getArticlesResult
    }

    override fun getArticle(articleNumber: Int): Flow<ResultState<Article>> {
        getArticleCallCount += 1
        lastGetArticleNumber = articleNumber
        return getArticleResult
    }

    override fun createArticle(
        articleNumber: Int,
        articleName: String,
        count: Int?,
    ): Flow<ResultState<Article>> {
        lastCreateArticleNumber = articleNumber
        lastCreateArticleName = articleName
        lastCreateCount = count
        return createArticleResult
    }

    override fun updateArticle(
        articleNumber: Int,
        articleName: String?,
        count: Int?,
    ): Flow<ResultState<Article>> {
        lastUpdateArticleNumber = articleNumber
        lastUpdateArticleName = articleName
        lastUpdateCount = count
        return updateArticleResult
    }
}
