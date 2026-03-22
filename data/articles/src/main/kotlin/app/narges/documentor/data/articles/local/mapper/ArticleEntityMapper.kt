package app.narges.documentor.data.articles.local.mapper

import app.narges.documentor.data.articles.local.entity.ArticleEntity
import app.narges.documentor.data.articles.model.ArticleDTO

internal fun ArticleEntity.toDto(): ArticleDTO = ArticleDTO(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)

internal fun ArticleDTO.toEntity(): ArticleEntity = ArticleEntity(
    articleNumber = articleNumber,
    articleName = articleName,
    count = count,
)
