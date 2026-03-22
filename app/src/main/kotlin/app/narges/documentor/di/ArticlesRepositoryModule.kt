package app.narges.documentor.di

import android.content.Context
import app.narges.documentor.core.dispatcher.IoDispatcher
import app.narges.documentor.data.articles.di.ArticlesDataProvider
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArticlesRepositoryModule {

    @Provides
    @Singleton
    fun provideArticlesRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ArticlesRepository = ArticlesDataProvider.provideRepository(
        context = context,
        ioDispatcher = ioDispatcher,
    )
}
