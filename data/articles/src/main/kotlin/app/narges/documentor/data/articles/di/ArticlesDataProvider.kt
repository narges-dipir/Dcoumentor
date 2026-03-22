package app.narges.documentor.data.articles.di

import android.content.Context
import app.narges.documentor.core.dispatcher.IoDispatcher
import app.narges.documentor.data.articles.local.db.ArticlesDatabase
import app.narges.documentor.data.articles.local.source.ArticlesLocalDataSource
import app.narges.documentor.data.articles.network.RetrofitInstance
import app.narges.documentor.data.articles.repository.ArticlesRepositoryImpl
import app.narges.documentor.domain.articles.repository.ArticlesRepository
import kotlinx.coroutines.CoroutineDispatcher

object ArticlesDataProvider {
    fun provideRepository(
        context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
    ): ArticlesRepository {
        val database = ArticlesDatabase.getInstance(context)
        val localDataSource = ArticlesLocalDataSource(database.articleDao())

        return ArticlesRepositoryImpl(
            remoteApi = RetrofitInstance.api,
            localDataSource = localDataSource,
            ioDispatcher = ioDispatcher,
        )
    }
}
