package app.narges.documentor.core.worker.init

import androidx.hilt.work.HiltWorkerFactory
import app.narges.documentor.core.worker.sync.SyncBootstrapper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WorkerInitializerEntryPoint {
    fun workerFactory(): HiltWorkerFactory
    fun syncBootstrapper(): SyncBootstrapper
}
