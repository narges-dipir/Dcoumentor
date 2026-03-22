package app.narges.documentor.feature.articledetails.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.usecase.articledetails.GetArticleUseCase
import app.narges.documentor.domain.articles.usecase.articlemutation.UpdateArticleUseCase
import app.narges.documentor.feature.articledetails.state.ArticleCountUiState
import app.narges.documentor.feature.articledetails.state.contract.ArticleCountEvent
import app.narges.documentor.feature.articledetails.state.contract.ArticleCountIntent
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
class ArticleCountViewModel @Inject constructor(
    private val getArticleUseCase: GetArticleUseCase,
    private val updateArticleUseCase: UpdateArticleUseCase,
) : ViewModel() {

    private var currentArticleNumber: Int? = null

    private val _state = MutableStateFlow(ArticleCountUiState())
    val state: StateFlow<ArticleCountUiState> = _state

    private val _events = MutableSharedFlow<ArticleCountEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<ArticleCountEvent> = _events

    fun onIntent(intent: ArticleCountIntent) {
        when (intent) {
            is ArticleCountIntent.OnScreenEntered -> {
                if (currentArticleNumber != intent.articleNumber) {
                    currentArticleNumber = intent.articleNumber
                    _state.update { current ->
                        current.copy(
                            articleNumber = intent.articleNumber,
                            articleName = "",
                            countInput = "",
                            isLoading = false,
                            isSaving = false,
                            countError = null,
                            errorMessage = null,
                        )
                    }
                    loadArticle(intent.articleNumber)
                }
            }

            is ArticleCountIntent.OnCountChanged -> {
                _state.update { current ->
                    current.copy(countInput = intent.value.filter { it.isDigit() }, countError = null, errorMessage = null)
                }
            }

            ArticleCountIntent.OnSaveClicked -> saveCount()
        }
    }

    private fun loadArticle(articleNumber: Int) {
        viewModelScope.launch {
            getArticleUseCase(articleNumber).collectLatest { result ->
                when (result) {
                    ResultState.Loading -> {
                        _state.update { current -> current.copy(isLoading = true, errorMessage = null) }
                    }

                    is ResultState.Success.Data -> {
                        _state.update { current ->
                            current.copy(
                                articleName = result.value.articleName,
                                countInput = result.value.count?.toString().orEmpty(),
                                isLoading = false,
                                errorMessage = null,
                            )
                        }
                    }

                    ResultState.Success.Empty,
                    is ResultState.Success.Message -> {
                        _state.update { current -> current.copy(isLoading = false) }
                    }

                    is ResultState.Error -> {
                        _state.update { current ->
                            current.copy(isLoading = false, errorMessage = result.message ?: DEFAULT_LOAD_ERROR)
                        }
                    }
                }
            }
        }
    }

    private fun saveCount() {
        val articleNumber = currentArticleNumber ?: return
        val countText = _state.value.countInput
        val countValue = countText.toIntOrNull()
        if (countValue == null || countValue !in 0..999) {
            _state.update { current ->
                current.copy(countError = "Count must be between 0 and 999")
            }
            return
        }

        viewModelScope.launch {
            updateArticleUseCase(articleNumber = articleNumber, count = countValue).collectLatest { result ->
                when (result) {
                    ResultState.Loading -> {
                        _state.update { current -> current.copy(isSaving = true, errorMessage = null) }
                    }

                    is ResultState.Success.Data,
                    ResultState.Success.Empty,
                    is ResultState.Success.Message -> {
                        _state.update { current -> current.copy(isSaving = false) }
                        _events.tryEmit(ArticleCountEvent.NavigateBack)
                    }

                    is ResultState.Error -> {
                        _state.update { current ->
                            current.copy(isSaving = false, errorMessage = result.message ?: DEFAULT_SAVE_ERROR)
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_LOAD_ERROR = "Unable to load article"
        private const val DEFAULT_SAVE_ERROR = "Unable to save count"
    }
}
