package app.narges.documentor.data.articles.testutil

import app.narges.documentor.data.articles.api.ArticlesApi
import app.narges.documentor.data.articles.local.entity.ArticleEntity
import app.narges.documentor.data.articles.model.ArticleCursorPageResponse
import app.narges.documentor.data.articles.model.ArticleDTO
import app.narges.documentor.data.articles.model.CreateArticleRequest
import app.narges.documentor.data.articles.model.UpdateArticleRequest
import com.google.gson.JsonSyntaxException
import java.io.IOException

internal fun articleEntity(
    articleNumber: Int = 1000001,
    articleName: String = "Red Apple",
    count: Int? = 10,
): ArticleEntity = ArticleEntity(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)

internal fun articleDto(
    articleNumber: Int = 1000001,
    articleName: String = "Red Apple",
    count: Int? = 10,
): ArticleDTO = ArticleDTO(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)

internal fun articlePageResponse(
    cursor: String? = null,
    nextCursor: String? = null,
    limit: Int = 20,
    items: List<ArticleDTO> = listOf(articleDto()),
): ArticleCursorPageResponse = ArticleCursorPageResponse(
    cursor = cursor,
    nextCursor = nextCursor,
    limit = limit,
    items = items,
)

internal class FakeArticlesApi(
    private val pageResponse: ArticleCursorPageResponse = articlePageResponse(items = emptyList()),
    private val articleByNumber: MutableMap<Int, ArticleDTO> = mutableMapOf(),
) : ArticlesApi {
    var getArticlesCalls: Int = 0
    var getArticleCalls: Int = 0
    val getArticlesRequestedCursors: MutableList<String?> = mutableListOf()
    var cursorPageResponses: Map<String?, ArticleCursorPageResponse>? = null

    var getArticlesFailuresRemaining: Int = 0
    var getArticlesFailure: Throwable? = null

    var getArticleFailuresRemaining: Int = 0
    var getArticleFailure: Throwable? = null

    override suspend fun getArticles(cursor: String?, limit: Int): ArticleCursorPageResponse {
        getArticlesCalls += 1
        getArticlesRequestedCursors += cursor
        if (getArticlesFailuresRemaining > 0) {
            getArticlesFailuresRemaining -= 1
            throw (getArticlesFailure ?: IOException("network"))
        }
        if (getArticlesFailure is JsonSyntaxException) {
            throw getArticlesFailure as JsonSyntaxException
        }

        val cursorSpecific = cursorPageResponses?.get(cursor)
        val response = cursorSpecific ?: pageResponse
        return response.copy(cursor = cursor, limit = limit)
    }

    override suspend fun getArticle(articleNumber: Int): ArticleDTO {
        getArticleCalls += 1
        if (getArticleFailuresRemaining > 0) {
            getArticleFailuresRemaining -= 1
            throw (getArticleFailure ?: IOException("network"))
        }
        if (getArticleFailure is JsonSyntaxException) {
            throw getArticleFailure as JsonSyntaxException
        }

        return articleByNumber[articleNumber]
            ?: pageResponse.items.firstOrNull { it.articleNumber == articleNumber }
            ?: articleDto(articleNumber = articleNumber, articleName = "Single", count = null)
    }

    override suspend fun createArticle(request: CreateArticleRequest): ArticleDTO {
        val created = articleDto(
            articleNumber = request.articleNumber,
            articleName = request.articleName,
            count = request.count,
        )
        articleByNumber[created.articleNumber] = created
        return created
    }

    override suspend fun updateArticle(articleNumber: Int, request: UpdateArticleRequest): ArticleDTO {
        val current = articleByNumber[articleNumber] ?: articleDto(articleNumber = articleNumber)
        val updated = current.copy(
            articleName = request.articleName ?: current.articleName,
            count = request.count,
        )
        articleByNumber[articleNumber] = updated
        return updated
    }
}
