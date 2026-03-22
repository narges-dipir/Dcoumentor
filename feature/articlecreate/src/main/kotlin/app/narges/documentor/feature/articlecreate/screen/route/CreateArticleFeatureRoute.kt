package app.narges.documentor.feature.articlecreate.screen.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import app.narges.documentor.feature.articlecreate.screen.content.CreateArticleScreen
import app.narges.documentor.feature.articlecreate.state.contract.CreateArticleEvent
import app.narges.documentor.feature.articlecreate.state.contract.CreateArticleIntent
import app.narges.documentor.feature.articlecreate.viewmodel.CreateArticleViewModel

@Composable
fun CreateArticleFeatureRoute(
    onNavigateBack: () -> Unit,
) {
    val viewModel: CreateArticleViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel, onNavigateBack) {
        viewModel.events.collect { event ->
            if (event is CreateArticleEvent.NavigateBack) onNavigateBack()
        }
    }

    CreateArticleScreen(
        uiState = uiState,
        onArticleNameChanged = {
            viewModel.onIntent(CreateArticleIntent.OnArticleNameChanged(it))
        },
        onArticleNumberChanged = {
            viewModel.onIntent(CreateArticleIntent.OnArticleNumberChanged(it))
        },
        onSaveClicked = { viewModel.onIntent(CreateArticleIntent.OnSaveClicked) },
    )
}
