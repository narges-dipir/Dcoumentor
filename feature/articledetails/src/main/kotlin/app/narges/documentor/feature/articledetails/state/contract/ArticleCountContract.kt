package app.narges.documentor.feature.articledetails.state.contract

sealed interface ArticleCountIntent {
    data class OnScreenEntered(val articleNumber: Int) : ArticleCountIntent
    data class OnCountChanged(val value: String) : ArticleCountIntent
    data object OnSaveClicked : ArticleCountIntent
}

sealed interface ArticleCountEvent {
    data object NavigateBack : ArticleCountEvent
}
