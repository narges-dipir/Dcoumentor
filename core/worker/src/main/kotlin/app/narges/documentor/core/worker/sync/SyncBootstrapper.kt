package app.narges.documentor.core.worker.sync

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncBootstrapper @Inject constructor(
    private val syncWorkScheduler: SyncWorkSchedulerContract,
    private val mockPushSyncObserver: MockPushSyncObserverContract,
) {
    fun start() {
        syncWorkScheduler.schedulePeriodicFallbackSync()
        syncWorkScheduler.enqueuePushSync()
        mockPushSyncObserver.start()
    }
}
