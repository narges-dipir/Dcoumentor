package app.narges.documentor.feature.articledetails.state

data class ArticleCountUiState(
    val articleNumber: Int = 0,
    val articleName: String = "",
    val countInput: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val countError: String? = null,
    val errorMessage: String? = null,
)
