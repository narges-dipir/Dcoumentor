package app.narges.documentor.data.articles.model

data class CreateArticleRequest(
    val articleNumber: Int,
    val articleName: String,
    val count: Int? = null,
)
