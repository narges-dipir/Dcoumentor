package app.narges.documentor.data.articles.mapper

import app.narges.documentor.core.model.Article
import app.narges.documentor.core.model.ArticleCursorPage
import app.narges.documentor.data.articles.model.ArticleCursorPageResponse
import app.narges.documentor.data.articles.model.ArticleDTO

internal fun ArticleDTO.toDomain(): Article = Article(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)

internal fun ArticleCursorPageResponse.toDomain(): ArticleCursorPage = ArticleCursorPage(
    cursor = cursor,
    nextCursor = nextCursor,
    limit = limit,
    items = items.map { it.toDomain() },
)
