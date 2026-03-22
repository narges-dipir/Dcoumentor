package app.narges.documentor.feature.articledetails.screen.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import app.narges.documentor.feature.articledetails.screen.content.ArticleCountScreen
import app.narges.documentor.feature.articledetails.state.contract.ArticleCountEvent
import app.narges.documentor.feature.articledetails.state.contract.ArticleCountIntent
import app.narges.documentor.feature.articledetails.viewmodel.ArticleCountViewModel

@Composable
fun ArticleCountFeatureRoute(
    articleNumber: Int,
    onNavigateBack: () -> Unit,
) {
    val viewModel: ArticleCountViewModel = hiltViewModel(key = "article-count-$articleNumber")
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel, articleNumber) {
        viewModel.onIntent(ArticleCountIntent.OnScreenEntered(articleNumber))
    }

    LaunchedEffect(viewModel, onNavigateBack) {
        viewModel.events.collect { event ->
            if (event is ArticleCountEvent.NavigateBack) onNavigateBack()
        }
    }

    ArticleCountScreen(
        uiState = uiState,
        onCountChanged = { viewModel.onIntent(ArticleCountIntent.OnCountChanged(it)) },
        onSaveClicked = { viewModel.onIntent(ArticleCountIntent.OnSaveClicked) },
    )
}
