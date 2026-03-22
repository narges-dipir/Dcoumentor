package app.narges.documentor.core.model

data class ArticleCursorPage(
    val cursor: String?,
    val nextCursor: String?,
    val limit: Int,
    val items: List<Article>,
)
