package app.narges.documentor.core.worker.di

import app.narges.documentor.core.worker.sync.MockPushSyncObserver
import app.narges.documentor.core.worker.sync.MockPushSyncObserverContract
import app.narges.documentor.core.worker.sync.SyncWorkScheduler
import app.narges.documentor.core.worker.sync.SyncWorkSchedulerContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncBindingsModule {

    @Binds
    @Singleton
    abstract fun bindSyncWorkScheduler(impl: SyncWorkScheduler): SyncWorkSchedulerContract

    @Binds
    @Singleton
    abstract fun bindMockPushSyncObserver(impl: MockPushSyncObserver): MockPushSyncObserverContract
}
