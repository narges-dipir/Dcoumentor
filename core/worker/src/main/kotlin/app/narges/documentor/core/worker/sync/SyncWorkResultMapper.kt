package app.narges.documentor.core.worker.sync

import androidx.work.ListenableWorker
import app.narges.documentor.core.common.policy.isRetryable
import app.narges.documentor.core.result.ResultState

object SyncWorkResultMapper {
    fun map(state: ResultState<*>): ListenableWorker.Result = when (state) {
        is ResultState.Success<*> -> ListenableWorker.Result.success()
        is ResultState.Loading -> ListenableWorker.Result.retry()
        is ResultState.Error -> {
            if (state.type.isRetryable()) {
                ListenableWorker.Result.retry()
            } else {
                ListenableWorker.Result.failure()
            }
        }
    }
}
