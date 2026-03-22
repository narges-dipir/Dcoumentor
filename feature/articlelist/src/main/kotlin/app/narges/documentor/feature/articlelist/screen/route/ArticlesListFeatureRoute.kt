package app.narges.documentor.feature.articlelist.screen.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import app.narges.documentor.feature.articlelist.screen.content.ArticlesListScreen
import app.narges.documentor.feature.articlelist.state.contract.ArticlesListEvent
import app.narges.documentor.feature.articlelist.state.contract.ArticlesListIntent
import app.narges.documentor.feature.articlelist.viewmodel.ArticlesListViewModel

@Composable
fun ArticlesListFeatureRoute(
    onNavigateToCreate: () -> Unit,
    onNavigateToCount: (Int) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModel: ArticlesListViewModel = hiltViewModel()
    val uiState by viewModel.state.collectAsState()

    LaunchedEffect(viewModel, onNavigateToCreate, onNavigateToCount) {
        viewModel.events.collect { event ->
            when (event) {
                ArticlesListEvent.NavigateToCreateArticle -> onNavigateToCreate()
                is ArticlesListEvent.NavigateToArticleCount -> onNavigateToCount(event.articleNumber)
            }
        }
    }

    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(ArticlesListIntent.OnScreenResumed)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    ArticlesListScreen(
        uiState = uiState,
        onAddClicked = { viewModel.onIntent(ArticlesListIntent.OnAddClicked) },
        onArticleClicked = { viewModel.onIntent(ArticlesListIntent.OnArticleClicked(it)) },
        onRetryClicked = { viewModel.onIntent(ArticlesListIntent.OnRetryClicked) },
        onLoadMoreClicked = { viewModel.onIntent(ArticlesListIntent.OnLoadMoreClicked) },
    )
}
