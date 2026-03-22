package app.narges.documentor.data.articles.model

data class ArticleCursorPageResponse(
    val cursor: String?,
    val nextCursor: String?,
    val limit: Int,
    val items: List<ArticleDTO>,
)
