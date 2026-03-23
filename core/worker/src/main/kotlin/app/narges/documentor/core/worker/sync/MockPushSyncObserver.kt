package app.narges.documentor.core.worker.sync

import app.narges.documentor.core.dispatcher.IoDispatcher
import app.narges.documentor.data.articles.fake.ArticlesSyncInvalidationSource
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class MockPushSyncObserver @Inject constructor(
    private val source: ArticlesSyncInvalidationSource,
    private val syncWorkScheduler: SyncWorkSchedulerContract,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : MockPushSyncObserverContract {
    private val started = AtomicBoolean(false)
    private val observeScope = CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun start() {
        if (!started.compareAndSet(false, true)) return

        observeScope.launch {
            source.invalidations.collect {
                syncWorkScheduler.enqueuePushSync()
            }
        }
    }
}
