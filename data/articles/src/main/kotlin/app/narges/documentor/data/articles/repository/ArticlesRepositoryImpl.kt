package app.narges.documentor.data.articles.repository

import android.database.sqlite.SQLiteException
import app.narges.documentor.core.common.error.toAppError
import app.narges.documentor.core.common.policy.RetryPolicy
import app.narges.documentor.core.common.policy.incrementalRetryDelayMs
import app.narges.documentor.core.common.policy.isRetryable
import app.narges.documentor.core.dispatcher.IoDispatcher
import app.narges.documentor.core.model.article.Article
import app.narges.documentor.core.model.article.ArticleCursorPage
import app.narges.documentor.core.result.ErrorState
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.data.articles.api.ArticlesApi
import app.narges.documentor.data.articles.local.source.ArticlesLocalDataSource
import app.narges.documentor.data.articles.mapper.toDomain
import app.narges.documentor.data.articles.model.CreateArticleRequest
import app.narges.documentor.data.articles.model.UpdateArticleRequest
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen

class ArticlesRepositoryImpl(
    private val remoteApi: ArticlesApi,
    private val localDataSource: ArticlesLocalDataSource,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ArticlesRepository {

    override fun getArticles(cursor: String?, limit: Int): Flow<ResultState<ArticleCursorPage>> =
        flow<ResultState<ArticleCursorPage>> {
            val cached = localDataSource.getArticles(cursor = cursor, limit = limit)
            if (cached.items.isNotEmpty()) {
                emit(ResultState.Success.Data(cached.toDomain()))
                return@flow
            }

            val remote = fetchWithRetry {
                remoteApi.getArticles(cursor = cursor, limit = limit)
            }
            if (remote.items.isNotEmpty()) {
                localDataSource.upsertArticles(remote.items)
                emit(ResultState.Success.Data(remote.toDomain()))
            } else {
                emit(ResultState.Success.Empty)
            }
        }
            .onStart { emit(ResultState.Loading) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(throwable.toResultError())
            }
            .flowOn(ioDispatcher)

    override fun getArticle(articleNumber: Int): Flow<ResultState<Article>> =
        flow<ResultState<Article>> {
            val cached = localDataSource.getArticle(articleNumber)
            if (cached != null) {
                emit(ResultState.Success.Data(cached.toDomain()))
                return@flow
            }

            val remote = fetchWithRetry {
                remoteApi.getArticle(articleNumber = articleNumber)
            }
            localDataSource.upsertArticle(remote)
            emit(ResultState.Success.Data(remote.toDomain()))
        }
            .onStart { emit(ResultState.Loading) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(throwable.toResultError())
            }
            .flowOn(ioDispatcher)

    override fun createArticle(
        articleNumber: Int,
        articleName: String,
        count: Int?,
    ): Flow<ResultState<Article>> =
        flow<ResultState<Article>> {
            val created = fetchWithRetry {
                remoteApi.createArticle(
                    request = CreateArticleRequest(
                        articleNumber = articleNumber,
                        articleName = articleName,
                        count = count,
                    ),
                )
            }
            localDataSource.upsertArticle(created)
            emit(ResultState.Success.Data(created.toDomain()))
        }
            .onStart { emit(ResultState.Loading) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(throwable.toResultError())
            }
            .flowOn(ioDispatcher)

    override fun updateArticle(
        articleNumber: Int,
        articleName: String?,
        count: Int?,
    ): Flow<ResultState<Article>> =
        flow<ResultState<Article>> {
            val updated = fetchWithRetry {
                remoteApi.updateArticle(
                    articleNumber = articleNumber,
                    request = UpdateArticleRequest(
                        articleName = articleName,
                        count = count,
                    ),
                )
            }
            localDataSource.upsertArticle(updated)
            emit(ResultState.Success.Data(updated.toDomain()))
        }
            .onStart { emit(ResultState.Loading) }
            .catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(throwable.toResultError())
            }
            .flowOn(ioDispatcher)

    private fun Throwable.toResultError(): ResultState.Error = ResultState.Error(
        type = toErrorState(),
        message = message,
        cause = this,
    )

    private suspend fun <T> fetchWithRetry(block: suspend () -> T): T {
        return flow { emit(block()) }
            .retryWhen { throwable, attempt ->
                val retryNumber = (attempt + 1).toInt()
                if (retryNumber > RetryPolicy.MAX_SERVER_RETRIES) return@retryWhen false
                if (throwable is CancellationException) throw throwable

                val error = throwable.toErrorState()
                if (!error.isRetryable()) return@retryWhen false

                val delayMs = incrementalRetryDelayMs(retryNumber)
                if (delayMs > 0L) delay(delayMs)
                true
            }
            .first()
    }

    private fun Throwable.toErrorState(): ErrorState = when (this) {
        is JsonParseException,
        is JsonSyntaxException,
        is MalformedJsonException -> ErrorState.Serialization
        is IllegalArgumentException -> ErrorState.Validation
        is SQLiteException -> ErrorState.Database
        else -> toAppError()
    }
}
