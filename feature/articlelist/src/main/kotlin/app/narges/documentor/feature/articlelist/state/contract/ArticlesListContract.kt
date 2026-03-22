package app.narges.documentor.feature.articlelist.state.contract

sealed interface ArticlesListIntent {
    data object OnScreenResumed : ArticlesListIntent
    data object OnAddClicked : ArticlesListIntent
    data class OnArticleClicked(val articleNumber: Int) : ArticlesListIntent
    data object OnLoadMoreClicked : ArticlesListIntent
    data object OnRetryClicked : ArticlesListIntent
}

sealed interface ArticlesListEvent {
    data object NavigateToCreateArticle : ArticlesListEvent
    data class NavigateToArticleCount(val articleNumber: Int) : ArticlesListEvent
}
