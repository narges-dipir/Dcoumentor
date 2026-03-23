package app.narges.documentor.data.articles.fake

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

interface ArticlesSyncInvalidationSource {
    val invalidations: SharedFlow<Unit>
}

object FakeArticlesSyncInvalidationBus : ArticlesSyncInvalidationSource {
    private val internalInvalidations = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 64,
    )

    override val invalidations: SharedFlow<Unit> = internalInvalidations.asSharedFlow()

    fun notifyStoreChanged() {
        internalInvalidations.tryEmit(Unit)
    }
}
