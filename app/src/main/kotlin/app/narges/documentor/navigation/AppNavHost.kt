package app.narges.documentor.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.narges.documentor.core.navigation.destination.ArticleCountDestination
import app.narges.documentor.core.navigation.destination.ArticlesListDestination
import app.narges.documentor.core.navigation.destination.CreateArticleDestination
import app.narges.documentor.feature.articlecreate.screen.route.CreateArticleFeatureRoute
import app.narges.documentor.feature.articlelist.screen.route.ArticlesListFeatureRoute
import app.narges.documentor.feature.articledetails.screen.route.ArticleCountFeatureRoute
import kotlin.collections.removeLastOrNull

@Composable
fun AppNavHost() {
    val backStack = remember { NavBackStack<NavKey>(ArticlesListDestination) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<ArticlesListDestination> {
                ArticlesListFeatureRoute(
                    onNavigateToCreate = {
                        backStack.add(CreateArticleDestination)
                    },
                    onNavigateToCount = { articleNumber ->
                        backStack.add(ArticleCountDestination(articleNumber = articleNumber))
                    },
                )
            }

            entry<CreateArticleDestination> {
                CreateArticleFeatureRoute(
                    onNavigateBack = { backStack.removeLastOrNull() },
                )
            }

            entry<ArticleCountDestination> { destination ->
                ArticleCountFeatureRoute(
                    articleNumber = destination.articleNumber,
                    onNavigateBack = { backStack.removeLastOrNull() },
                )
            }
        }
    )
}
