package app.narges.documentor.data.articles.local.source

import app.narges.documentor.data.articles.local.dao.ArticleDao
import app.narges.documentor.data.articles.local.mapper.toDto
import app.narges.documentor.data.articles.local.mapper.toEntity
import app.narges.documentor.data.articles.model.ArticleCursorPageResponse
import app.narges.documentor.data.articles.model.ArticleDTO

class ArticlesLocalDataSource(
    private val articleDao: ArticleDao,
) {
    suspend fun getArticles(cursor: String?, limit: Int): ArticleCursorPageResponse {
        require(limit > 0) { "limit must be > 0" }

        val offset = cursor?.toIntOrNull() ?: 0
        require(offset >= 0) { "cursor must be a non-negative integer offset" }

        val items = articleDao.getByOffset(limit = limit, offset = offset).map { it.toDto() }
        val total = articleDao.count()
        val nextOffset = offset + items.size

        return ArticleCursorPageResponse(
            cursor = cursor,
            nextCursor = if (nextOffset < total) nextOffset.toString() else null,
            limit = limit,
            items = items,
        )
    }

    suspend fun getArticle(articleNumber: Int): ArticleDTO? {
        return articleDao.getByNumber(articleNumber)?.toDto()
    }

    suspend fun upsertArticle(article: ArticleDTO) {
        articleDao.upsert(article.toEntity())
    }

    suspend fun upsertArticles(articles: List<ArticleDTO>) {
        articleDao.upsertAll(articles.map { it.toEntity() })
    }

    suspend fun clearAll() {
        articleDao.clearAll()
    }
}
