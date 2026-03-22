package app.narges.documentor.core.model.article

data class ArticleCursorPage(
    val cursor: String?,
    val nextCursor: String?,
    val limit: Int,
    val items: List<Article>,
)
