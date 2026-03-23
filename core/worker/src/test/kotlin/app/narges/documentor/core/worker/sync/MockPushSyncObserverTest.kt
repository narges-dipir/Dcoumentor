package app.narges.documentor.core.worker.sync

import app.narges.documentor.data.articles.fake.ArticlesSyncInvalidationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Test

class MockPushSyncObserverTest {

    @Test
    fun `observer enqueues push sync on invalidation`() = runBlocking {
        val source = FakeInvalidationSource()
        val scheduler = FakeSyncWorkScheduler()
        val observer = MockPushSyncObserver(
            source = source,
            syncWorkScheduler = scheduler,
            ioDispatcher = Dispatchers.Unconfined,
        )
        observer.start()

        source.emitInvalidation()

        withTimeout(1_000) {
            while (scheduler.pushCalls == 0) {
                kotlinx.coroutines.yield()
            }
        }
        assertEquals(1, scheduler.pushCalls)
    }

    @Test
    fun `observer start is idempotent`() = runBlocking {
        val source = FakeInvalidationSource()
        val scheduler = FakeSyncWorkScheduler()
        val observer = MockPushSyncObserver(
            source = source,
            syncWorkScheduler = scheduler,
            ioDispatcher = Dispatchers.Unconfined,
        )

        observer.start()
        observer.start()
        source.emitInvalidation()

        withTimeout(1_000) {
            while (scheduler.pushCalls == 0) {
                kotlinx.coroutines.yield()
            }
        }
        assertEquals(1, scheduler.pushCalls)
    }

    private class FakeSyncWorkScheduler : SyncWorkSchedulerContract {
        var pushCalls: Int = 0

        override fun enqueuePushSync() {
            pushCalls += 1
        }

        override fun schedulePeriodicFallbackSync() = Unit
    }

    private class FakeInvalidationSource : ArticlesSyncInvalidationSource {
        private val bus = MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 8,
        )

        override val invalidations: SharedFlow<Unit> = bus

        fun emitInvalidation() {
            bus.tryEmit(Unit)
        }
    }
}
