package app.narges.documentor.feature.articlelist.screen.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.narges.documentor.feature.articlelist.state.ArticlesListUiState

@Composable
fun ArticlesListScreen(
    uiState: ArticlesListUiState,
    onAddClicked: () -> Unit,
    onArticleClicked: (Int) -> Unit,
    onRetryClicked: () -> Unit,
    onLoadMoreClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClicked,
                modifier = Modifier.testTag("add_article_button"),
            ) {
                Text(text = "+")
            }
        },
    ) { contentPadding ->
        when {
            uiState.isLoading && uiState.articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.articles.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(uiState.errorMessage)
                    Button(
                        onClick = onRetryClicked,
                        modifier = Modifier.testTag("retry_button"),
                    ) {
                        Text("Retry")
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        items = uiState.articles,
                        key = { it.articleNumber },
                    ) { article ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("article_row_${article.articleNumber}")
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { onArticleClicked(article.articleNumber) }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column {
                                Text(text = article.articleName, style = MaterialTheme.typography.titleMedium)
                                Text(text = article.articleNumber.toString(), style = MaterialTheme.typography.bodyMedium)
                            }

                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .testTag("article_status_${article.articleNumber}_${if (article.count == null) "red" else "green"}")
                                    .clip(MaterialTheme.shapes.small)
                                    .background(
                                        if (article.count == null) {
                                            Color(0xFFD32F2F)
                                        } else {
                                            Color(0xFF2E7D32)
                                        },
                                    ),
                            )
                        }
                    }

                    if (uiState.nextCursor != null) {
                        item {
                            Button(
                                onClick = onLoadMoreClicked,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("load_more_button"),
                                enabled = !uiState.isLoadingMore,
                            ) {
                                if (uiState.isLoadingMore) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                } else {
                                    Text("Load More")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
