package app.narges.documentor.data.articles.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val articleNumber: Int,
    val articleName: String,
    val count: Int?,
)
