package app.narges.documentor.feature.articlecreate.state.contract

sealed interface CreateArticleIntent {
    data class OnArticleNameChanged(val value: String) : CreateArticleIntent
    data class OnArticleNumberChanged(val value: String) : CreateArticleIntent
    data object OnSaveClicked : CreateArticleIntent
}

sealed interface CreateArticleEvent {
    data object NavigateBack : CreateArticleEvent
}
