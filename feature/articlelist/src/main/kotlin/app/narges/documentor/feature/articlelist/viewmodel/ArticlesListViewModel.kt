package app.narges.documentor.feature.articlelist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.usecase.articlelist.GetArticlesUseCase
import app.narges.documentor.feature.articlelist.state.ArticlesListUiState
import app.narges.documentor.feature.articlelist.state.contract.ArticlesListEvent
import app.narges.documentor.feature.articlelist.state.contract.ArticlesListIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesListViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ArticlesListUiState())
    val state: StateFlow<ArticlesListUiState> = _state

    private val _events = MutableSharedFlow<ArticlesListEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<ArticlesListEvent> = _events

    init {
        loadPage(cursor = null, append = false)
    }

    fun onIntent(intent: ArticlesListIntent) {
        when (intent) {
            ArticlesListIntent.OnScreenResumed -> loadPage(cursor = null, append = false)
            ArticlesListIntent.OnAddClicked -> _events.tryEmit(ArticlesListEvent.NavigateToCreateArticle)
            is ArticlesListIntent.OnArticleClicked -> {
                _events.tryEmit(ArticlesListEvent.NavigateToArticleCount(intent.articleNumber))
            }
            ArticlesListIntent.OnLoadMoreClicked -> {
                val cursor = _state.value.nextCursor ?: return
                loadPage(cursor = cursor, append = true)
            }
            ArticlesListIntent.OnRetryClicked -> loadPage(cursor = null, append = false)
        }
    }

    private fun loadPage(cursor: String?, append: Boolean) {
        if (_state.value.isLoading || _state.value.isLoadingMore) return

        viewModelScope.launch {
            getArticlesUseCase(cursor = cursor, limit = PAGE_LIMIT).collectLatest { result ->
                when (result) {
                    ResultState.Loading -> {
                        _state.update { current ->
                            if (append) {
                                current.copy(isLoadingMore = true, errorMessage = null)
                            } else {
                                current.copy(isLoading = true, errorMessage = null)
                            }
                        }
                    }

                    is ResultState.Success.Data -> {
                        _state.update { current ->
                            val merged = if (append) current.articles + result.value.items else result.value.items
                            current.copy(
                                articles = merged,
                                nextCursor = result.value.nextCursor,
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = null,
                            )
                        }
                    }

                    ResultState.Success.Empty -> {
                        _state.update { current ->
                            current.copy(
                                articles = if (append) current.articles else emptyList(),
                                nextCursor = null,
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = null,
                            )
                        }
                    }

                    is ResultState.Success.Message -> {
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = null,
                            )
                        }
                    }

                    is ResultState.Error -> {
                        _state.update { current ->
                            current.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                errorMessage = result.message ?: DEFAULT_ERROR,
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val PAGE_LIMIT = 20
        private const val DEFAULT_ERROR = "Unable to load articles"
    }
}
