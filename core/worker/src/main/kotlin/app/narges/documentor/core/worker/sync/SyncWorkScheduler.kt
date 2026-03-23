package app.narges.documentor.core.worker.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncWorkScheduler @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : SyncWorkSchedulerContract {
    private val workManager: WorkManager by lazy { WorkManager.getInstance(context) }

    override fun enqueuePushSync() {
        val workRequest = OneTimeWorkRequestBuilder<ArticlesSyncWorker>()
            .setConstraints(networkConstraint())
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            PUSH_SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest,
        )
    }

    override fun schedulePeriodicFallbackSync() {
        val periodicRequest = PeriodicWorkRequestBuilder<ArticlesSyncWorker>(
            PERIODIC_SYNC_HOURS,
            TimeUnit.HOURS,
        )
            .setConstraints(networkConstraint())
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest,
        )
    }

    private fun networkConstraint(): Constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    companion object {
        private const val PERIODIC_SYNC_HOURS = 6L
        private const val PUSH_SYNC_WORK_NAME = "articles_push_sync"
        private const val PERIODIC_SYNC_WORK_NAME = "articles_periodic_sync"
    }
}
