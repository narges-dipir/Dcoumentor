package app.narges.documentor.data.articles.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import app.narges.documentor.data.articles.local.dao.ArticleDao
import app.narges.documentor.data.articles.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class ArticlesDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao

    companion object {
        private const val DB_NAME = "articles_cache.db"

        @Volatile
        private var instance: ArticlesDatabase? = null

        fun getInstance(context: Context): ArticlesDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ArticlesDatabase::class.java,
                    DB_NAME,
                ).build().also { instance = it }
            }
        }
    }
}
