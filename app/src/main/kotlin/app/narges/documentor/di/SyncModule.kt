package app.narges.documentor.di

import app.narges.documentor.data.articles.fake.ArticlesSyncInvalidationSource
import app.narges.documentor.data.articles.fake.FakeArticlesSyncInvalidationBus
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SyncModule {

    @Provides
    @Singleton
    fun provideArticlesSyncInvalidationSource(): ArticlesSyncInvalidationSource {
        return FakeArticlesSyncInvalidationBus
    }
}
