package app.narges.documentor.core.worker.init

import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.android.EntryPointAccessors

class DocumentorSyncInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        val appContext = context.applicationContext
        val bootstrapper = EntryPointAccessors
            .fromApplication(appContext, WorkerInitializerEntryPoint::class.java)
            .syncBootstrapper()
        bootstrapper.start()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(DocumentorWorkManagerInitializer::class.java)
    }
}
