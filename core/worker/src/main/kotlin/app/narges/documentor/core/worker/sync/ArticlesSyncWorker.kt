package app.narges.documentor.core.worker.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import app.narges.documentor.core.result.ResultState
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ArticlesSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val articlesRepository: ArticlesRepository,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val syncState = articlesRepository.syncArticles().first { state ->
            state !is ResultState.Loading
        }
        return SyncWorkResultMapper.map(syncState)
    }
}
