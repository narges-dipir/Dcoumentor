package app.narges.documentor.core.navigation.destination

import androidx.navigation3.runtime.NavKey

data object ArticlesListDestination : NavKey

data object CreateArticleDestination : NavKey

data class ArticleCountDestination(
    val articleNumber: Int,
) : NavKey
