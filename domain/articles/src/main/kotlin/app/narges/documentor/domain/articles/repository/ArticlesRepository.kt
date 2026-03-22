package app.narges.documentor.domain.articles.repository

import app.narges.documentor.core.model.Article
import app.narges.documentor.core.model.ArticleCursorPage
import app.narges.documentor.core.result.ResultState
import kotlinx.coroutines.flow.Flow

interface ArticlesRepository {
    fun getArticles(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>>

    fun getArticle(articleNumber: Int): Flow<ResultState<Article>>

    fun createArticle(
        articleNumber: Int,
        articleName: String,
        count: Int? = null,
    ): Flow<ResultState<Article>>

    fun updateArticle(
        articleNumber: Int,
        articleName: String? = null,
        count: Int? = null,
    ): Flow<ResultState<Article>>
}
