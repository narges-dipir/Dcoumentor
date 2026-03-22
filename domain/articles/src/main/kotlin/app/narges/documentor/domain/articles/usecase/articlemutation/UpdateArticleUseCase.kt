package app.narges.documentor.domain.articles.usecase.articlemutation

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateArticleUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository,
) {
    operator fun invoke(
        articleNumber: Int,
        articleName: String? = null,
        count: Int? = null,
    ): Flow<ResultState<Article>> =
        articlesRepository.updateArticle(
            articleNumber = articleNumber,
            articleName = articleName,
            count = count,
        )
}
