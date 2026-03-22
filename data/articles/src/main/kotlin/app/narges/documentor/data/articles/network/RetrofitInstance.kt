package app.narges.documentor.data.articles.network

import app.narges.documentor.data.articles.api.ArticlesApi
import app.narges.documentor.data.articles.fake.FakeArticleApiInterceptor
import app.narges.documentor.data.articles.fake.FakeArticleBackend
import app.narges.documentor.data.articles.fake.SeedArticlesJson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://fake-articles.local/"

    private val gson = GsonBuilder().create()
    private val backend = FakeArticleBackend(seedJson = SeedArticlesJson.value, gson = gson)

    private val fakeClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(FakeArticleApiInterceptor(backend = backend, gson = gson))
            .build()
    }

    val api: ArticlesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(fakeClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ArticlesApi::class.java)
    }
}
