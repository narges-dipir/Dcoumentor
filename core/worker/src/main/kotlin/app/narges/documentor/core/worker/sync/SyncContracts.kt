package app.narges.documentor.core.worker.sync

interface SyncWorkSchedulerContract {
    fun enqueuePushSync()
    fun schedulePeriodicFallbackSync()
}

interface MockPushSyncObserverContract {
    fun start()
}
