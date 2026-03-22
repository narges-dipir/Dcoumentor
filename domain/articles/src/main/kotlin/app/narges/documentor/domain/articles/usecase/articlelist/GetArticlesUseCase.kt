package app.narges.documentor.domain.articles.usecase.articlelist

import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository,
) {
    operator fun invoke(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>> =
        articlesRepository.getArticles(cursor = cursor, limit = limit)
}
