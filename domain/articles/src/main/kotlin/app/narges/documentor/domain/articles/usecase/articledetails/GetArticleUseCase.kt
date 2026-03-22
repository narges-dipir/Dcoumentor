package app.narges.documentor.domain.articles.usecase.articledetails

import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticleUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository,
) {
    operator fun invoke(articleNumber: Int): Flow<ResultState<Article>> =
        articlesRepository.getArticle(articleNumber = articleNumber)
}
