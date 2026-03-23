package app.narges.documentor.core.worker.init

import android.content.Context
import androidx.startup.Initializer
import androidx.work.Configuration
import androidx.work.WorkManager
import dagger.hilt.android.EntryPointAccessors

class DocumentorWorkManagerInitializer : Initializer<WorkManager> {
    override fun create(context: Context): WorkManager {
        val appContext = context.applicationContext
        val workerFactory = entryPoint(appContext).workerFactory()
        val configuration = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(appContext, configuration)
        return WorkManager.getInstance(appContext)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    private fun entryPoint(context: Context): WorkerInitializerEntryPoint {
        return EntryPointAccessors.fromApplication(context, WorkerInitializerEntryPoint::class.java)
    }
}
