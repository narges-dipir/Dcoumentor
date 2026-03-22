package app.narges.documentor.feature.articlecreate.state

data class CreateArticleUiState(
    val articleName: String = "",
    val articleNumberInput: String = "",
    val isSaving: Boolean = false,
    val articleNameError: String? = null,
    val articleNumberError: String? = null,
    val errorMessage: String? = null,
)
