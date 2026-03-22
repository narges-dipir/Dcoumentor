package app.narges.documentor.feature.articlelist.state

import app.narges.documentor.core.model.article.Article

data class ArticlesListUiState(
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val nextCursor: String? = null,
    val errorMessage: String? = null,
)
