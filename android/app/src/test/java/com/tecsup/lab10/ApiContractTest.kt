package com.tecsup.lab10

import com.tecsup.lab10.data.*
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

class ApiContractTest {
    private lateinit var server: MockWebServer
    private lateinit var api: SerieApiService
    @Before fun setup() { server = MockWebServer(); server.start(); api = ApiClient.retrofit(server.url("/").toString()).create(SerieApiService::class.java) }
    @After fun close() { server.shutdown() }
    private val json = """{"id":4,"name":"Dark","release_date":"2017-12-01","rating":9,"category":"Misterio"}"""

    @Test fun seriesContractUsesCorrectVerbsPathsAndJson(): Unit = runBlocking {
        val repo = SeriesRepository(api)
        server.enqueue(MockResponse().setBody("[$json]"))
        assertEquals("Dark", repo.list().single().values.first())
        assertEquals("GET /serie HTTP/1.1", server.takeRequest().requestLine)
        server.enqueue(MockResponse().setBody(json))
        assertEquals(4, repo.get(4).id)
        assertEquals("GET /serie/4 HTTP/1.1", server.takeRequest().requestLine)
        server.enqueue(MockResponse().setResponseCode(201).setBody(json))
        repo.save(0, listOf("Dark", "2017-12-01", "9", "Misterio"))
        val post = server.takeRequest()
        assertEquals("POST", post.method)
        assertTrue(post.body.readUtf8().contains("\"release_date\":\"2017-12-01\""))
        server.enqueue(MockResponse().setBody(json))
        repo.save(4, listOf("Dark", "2017-12-01", "9", "Misterio"))
        assertEquals("PUT /serie/4 HTTP/1.1", server.takeRequest().requestLine)
        server.enqueue(MockResponse().setResponseCode(204))
        repo.delete(4)
        assertEquals("DELETE /serie/4 HTTP/1.1", server.takeRequest().requestLine)
    }

    @Test fun httpFailureIsNotReportedAsSuccess(): Unit = runBlocking {
        server.enqueue(MockResponse().setResponseCode(500))
        try { SeriesRepository(api).delete(3); fail("Expected HTTP error") } catch (e: HttpException) { assertEquals(500, e.code()) }
    }

    @Test fun rejectsImpossibleDatesAndInvalidNumbers() {
        val repo = SeriesRepository(api)
        assertNotNull(repo.validate(listOf("A", "2026-02-30", "5", "Drama")))
        assertNotNull(repo.validate(listOf("A", "2026-02-20", "texto", "Drama")))
        assertNotNull(repo.validate(listOf("A", "2026-02-20", "11", "Drama")))
        assertNull(repo.validate(listOf("A", "2024-02-29", "10", "Drama")))
    }

    @Test fun medicationCrudUsesItsOwnEndpoint(): Unit = runBlocking {
        val medApi = ApiClient.retrofit(server.url("/").toString()).create(MedicamentoApiService::class.java)
        val repo = MedicamentosRepository(medApi)
        val body = """{"id":1,"nombre":"Demo","dosis":"Prueba","frecuencia":"Prueba","cantidad":20}"""
        server.enqueue(MockResponse().setBody("[$body]")); assertEquals(1,repo.list().size)
        assertEquals("/medicamento",server.takeRequest().path)
        server.enqueue(MockResponse().setBody(body)); repo.get(1)
        assertEquals("GET",server.takeRequest().method)
        server.enqueue(MockResponse().setResponseCode(201).setBody(body)); repo.save(0,listOf("Demo","Prueba","Prueba","20"))
        assertEquals("POST",server.takeRequest().method)
        server.enqueue(MockResponse().setBody(body)); repo.save(1,listOf("Demo","Prueba","Prueba","20"))
        assertEquals("PUT",server.takeRequest().method)
        server.enqueue(MockResponse().setResponseCode(204)); repo.delete(1)
        assertEquals("DELETE",server.takeRequest().method)
        assertNotNull(repo.validate(listOf("Demo","Prueba","Prueba","-1")))
    }

    @Test fun bookshelfRepositorySupportsSearchAndDetail(): Unit = runBlocking {
        val books = BooksRepository(ApiClient.retrofit(server.url("/").toString()).create(BooksApiService::class.java))
        server.enqueue(MockResponse().setBody("""{"items":[{"id":"abc","volumeInfo":{"title":"Kotlin"}}]}"""))
        assertEquals("abc", books.search("kotlin").single().id)
        assertTrue(server.takeRequest().path!!.startsWith("/volumes?q=kotlin"))
        server.enqueue(MockResponse().setBody("""{"id":"abc","volumeInfo":{"title":"Kotlin","imageLinks":{"thumbnail":"http://books.google.com/cover"}}}"""))
        assertEquals("https://books.google.com/cover", books.detail("abc").volumeInfo.imageLinks!!.secureThumbnail())
        assertEquals("/volumes/abc",server.takeRequest().path)
    }
}
