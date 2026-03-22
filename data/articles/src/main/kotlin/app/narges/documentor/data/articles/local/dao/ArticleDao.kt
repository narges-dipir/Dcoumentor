package app.narges.documentor.data.articles.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.narges.documentor.data.articles.local.entity.ArticleEntity

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles ORDER BY articleNumber ASC LIMIT :limit OFFSET :offset")
    suspend fun getByOffset(limit: Int, offset: Int): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE articleNumber = :articleNumber LIMIT 1")
    suspend fun getByNumber(articleNumber: Int): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: ArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(articles: List<ArticleEntity>)

    @Query("SELECT COUNT(*) FROM articles")
    suspend fun count(): Int

    @Query("DELETE FROM articles")
    suspend fun clearAll()
}
