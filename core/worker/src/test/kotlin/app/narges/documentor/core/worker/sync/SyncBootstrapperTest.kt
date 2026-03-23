package app.narges.documentor.core.worker.sync

import org.junit.Assert.assertEquals
import org.junit.Test

class SyncBootstrapperTest {

    @Test
    fun `start schedules periodic sync push sync and observer`() {
        val scheduler = FakeSyncWorkScheduler()
        val observer = FakeMockPushSyncObserver()
        val bootstrapper = SyncBootstrapper(
            syncWorkScheduler = scheduler,
            mockPushSyncObserver = observer,
        )

        bootstrapper.start()

        assertEquals(1, scheduler.periodicCalls)
        assertEquals(1, scheduler.pushCalls)
        assertEquals(1, observer.startCalls)
    }

    private class FakeSyncWorkScheduler : SyncWorkSchedulerContract {
        var periodicCalls: Int = 0
        var pushCalls: Int = 0

        override fun enqueuePushSync() {
            pushCalls += 1
        }

        override fun schedulePeriodicFallbackSync() {
            periodicCalls += 1
        }
    }

    private class FakeMockPushSyncObserver : MockPushSyncObserverContract {
        var startCalls: Int = 0

        override fun start() {
            startCalls += 1
        }
    }
}
