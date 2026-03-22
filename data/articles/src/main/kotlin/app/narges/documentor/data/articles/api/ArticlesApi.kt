package app.narges.documentor.data.articles.api

import app.narges.documentor.data.articles.model.ArticleCursorPageResponse
import app.narges.documentor.data.articles.model.ArticleDTO
import app.narges.documentor.data.articles.model.CreateArticleRequest
import app.narges.documentor.data.articles.model.UpdateArticleRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticlesApi {
    @GET("articles")
    suspend fun getArticles(
        @Query("cursor") cursor: String?,
        @Query("limit") limit: Int,
    ): ArticleCursorPageResponse

    @GET("articles/{articleNumber}")
    suspend fun getArticle(
        @Path("articleNumber") articleNumber: Int,
    ): ArticleDTO

    @POST("articles")
    suspend fun createArticle(
        @Body request: CreateArticleRequest,
    ): ArticleDTO

    @PUT("articles/{articleNumber}")
    suspend fun updateArticle(
        @Path("articleNumber") articleNumber: Int,
        @Body request: UpdateArticleRequest,
    ): ArticleDTO
}
