package app.narges.documentor.feature.articlecreate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.usecase.articlemutation.CreateArticleUseCase
import app.narges.documentor.feature.articlecreate.state.CreateArticleUiState
import app.narges.documentor.feature.articlecreate.state.contract.CreateArticleEvent
import app.narges.documentor.feature.articlecreate.state.contract.CreateArticleIntent
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
class CreateArticleViewModel @Inject constructor(
    private val createArticleUseCase: CreateArticleUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CreateArticleUiState())
    val state: StateFlow<CreateArticleUiState> = _state

    private val _events = MutableSharedFlow<CreateArticleEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val events: SharedFlow<CreateArticleEvent> = _events

    fun onIntent(intent: CreateArticleIntent) {
        when (intent) {
            is CreateArticleIntent.OnArticleNameChanged -> {
                _state.update { current ->
                    current.copy(articleName = intent.value, articleNameError = null, errorMessage = null)
                }
            }

            is CreateArticleIntent.OnArticleNumberChanged -> {
                _state.update { current ->
                    current.copy(articleNumberInput = intent.value.filter { it.isDigit() }, articleNumberError = null, errorMessage = null)
                }
            }

            CreateArticleIntent.OnSaveClicked -> saveArticle()
        }
    }

    private fun saveArticle() {
        val state = _state.value
        val name = state.articleName.trim()
        val number = state.articleNumberInput

        val nameError = if (name.length < 3) "Name must be at least 3 characters" else null
        val numberError = if (number.length != 7) "Article number must be 7 digits" else null
        if (nameError != null || numberError != null) {
            _state.update { current ->
                current.copy(articleNameError = nameError, articleNumberError = numberError)
            }
            return
        }

        viewModelScope.launch {
            createArticleUseCase(
                articleNumber = number.toInt(),
                articleName = name,
                count = null,
            ).collectLatest { result ->
                when (result) {
                    ResultState.Loading -> {
                        _state.update { current -> current.copy(isSaving = true, errorMessage = null) }
                    }

                    is ResultState.Success.Data,
                    ResultState.Success.Empty,
                    is ResultState.Success.Message -> {
                        _state.update { current -> current.copy(isSaving = false) }
                        _events.tryEmit(CreateArticleEvent.NavigateBack)
                    }

                    is ResultState.Error -> {
                        _state.update { current ->
                            current.copy(
                                isSaving = false,
                                errorMessage = result.message ?: DEFAULT_ERROR,
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_ERROR = "Unable to create article"
    }
}
