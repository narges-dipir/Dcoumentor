package app.narges.documentor.data.articles.repository.mapper

import app.narges.documentor.data.articles.local.mapper.toDto
import app.narges.documentor.data.articles.local.mapper.toEntity
import app.narges.documentor.data.articles.mapper.toDomain
import app.narges.documentor.data.articles.testutil.articleDto
import app.narges.documentor.data.articles.testutil.articleEntity
import app.narges.documentor.data.articles.testutil.articlePageResponse
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticlesRepositoryMappersTest {

    @Test
    fun articleDto_mapsToArticleEntity() {
        val dto = articleDto(articleNumber = 1000007, articleName = "DTO Name", count = 4)

        val entity = dto.toEntity()

        assertEquals(1000007, entity.articleNumber)
        assertEquals("DTO Name", entity.articleName)
        assertEquals(4, entity.count)
    }

    @Test
    fun articleEntity_mapsToArticleDto() {
        val entity = articleEntity(articleNumber = 1000008, articleName = "Entity Name", count = null)

        val dto = entity.toDto()

        assertEquals(1000008, dto.articleNumber)
        assertEquals("Entity Name", dto.articleName)
        assertEquals(null, dto.count)
    }

    @Test
    fun articleResponseModels_mapToDomainModels() {
        val dto = articleDto(articleNumber = 1000009, articleName = "Domain Name", count = 9)
        val page = articlePageResponse(cursor = "0", nextCursor = "1", limit = 1, items = listOf(dto))

        val article = dto.toDomain()
        val domainPage = page.toDomain()

        assertEquals("Domain Name", article.articleName)
        assertEquals("0", domainPage.cursor)
        assertEquals("1", domainPage.nextCursor)
        assertEquals(1, domainPage.items.size)
        assertEquals(1000009, domainPage.items.first().articleNumber)
    }
}
