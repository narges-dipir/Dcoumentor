package app.narges.documentor.data.articles.fake

import app.narges.documentor.data.articles.model.ArticleCursorPageResponse
import app.narges.documentor.data.articles.model.ArticleDTO
import app.narges.documentor.data.articles.model.CreateArticleRequest
import app.narges.documentor.data.articles.model.UpdateArticleRequest
import com.google.gson.Gson

internal class FakeArticleBackend(
    seedJson: String,
    private val gson: Gson,
) {
    private val stateLock = Any()
    private val articles = gson.fromJson(seedJson, ArticleSeed::class.java)
        ?.articles
        ?.map { it.toRecord() }
        ?.toMutableList()
        ?: mutableListOf()

    fun getArticles(cursor: String?, limit: Int): ArticleCursorPageResponse {
        require(limit > 0) { "limit must be > 0" }

        val startIndex = cursor?.toIntOrNull() ?: 0
        require(startIndex >= 0) { "cursor must be a non-negative integer offset" }

        synchronized(stateLock) {
            if (startIndex > articles.size) {
                throw IllegalArgumentException("cursor is out of range")
            }

            val toIndex = minOf(startIndex + limit, articles.size)
            val items = if (startIndex >= articles.size) {
                emptyList()
            } else {
                articles.subList(startIndex, toIndex).map { it.toDto() }
            }
            val nextCursor = if (toIndex < articles.size) toIndex.toString() else null

            return ArticleCursorPageResponse(
                cursor = cursor,
                nextCursor = nextCursor,
                limit = limit,
                items = items,
            )
        }
    }

    fun getArticle(articleNumber: Int): ArticleDTO {
        synchronized(stateLock) {
            return articles.firstOrNull { it.articleNumber == articleNumber }?.toDto()
                ?: throw NoSuchElementException("article $articleNumber was not found")
        }
    }

    fun createArticle(request: CreateArticleRequest): ArticleDTO {
        validateArticleNumber(request.articleNumber)
        validateArticleName(request.articleName)
        request.count?.let(::validateArticleCount)

        synchronized(stateLock) {
            if (articles.any { it.articleNumber == request.articleNumber }) {
                throw IllegalArgumentException("article ${request.articleNumber} already exists")
            }

            val newRecord = ArticleRecord(
                articleNumber = request.articleNumber,
                articleName = request.articleName.trim(),
                count = request.count,
            )
            articles += newRecord
            FakeArticlesSyncInvalidationBus.notifyStoreChanged()
            return newRecord.toDto()
        }
    }

    fun updateArticle(articleNumber: Int, request: UpdateArticleRequest): ArticleDTO {
        request.articleName?.let(::validateArticleName)
        request.count?.let(::validateArticleCount)

        synchronized(stateLock) {
            val index = articles.indexOfFirst { it.articleNumber == articleNumber }
            if (index == -1) {
                throw NoSuchElementException("article $articleNumber was not found")
            }

            val current = articles[index]
            val updated = current.copy(
                articleName = request.articleName?.trim() ?: current.articleName,
                count = request.count ?: current.count,
            )
            articles[index] = updated
            FakeArticlesSyncInvalidationBus.notifyStoreChanged()
            return updated.toDto()
        }
    }

    private fun validateArticleNumber(articleNumber: Int) {
        require(articleNumber in 1_000_000..9_999_999) {
            "articleNumber must have exactly 7 digits"
        }
    }

    private fun validateArticleName(articleName: String) {
        require(articleName.trim().length >= 3) {
            "articleName must be at least 3 characters"
        }
    }

    private fun validateArticleCount(count: Int) {
        require(count in 0..999) {
            "count must be between 0 and 999"
        }
    }
}

internal data class ArticleSeed(
    val articles: List<SeedArticle> = emptyList(),
)

internal data class SeedArticle(
    val articleNumber: Int,
    val articleName: String,
    val count: Int?,
)

internal data class ArticleRecord(
    val articleNumber: Int,
    val articleName: String,
    val count: Int?,
)

internal fun SeedArticle.toRecord(): ArticleRecord = ArticleRecord(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)

internal fun ArticleRecord.toDto(): ArticleDTO = ArticleDTO(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)
