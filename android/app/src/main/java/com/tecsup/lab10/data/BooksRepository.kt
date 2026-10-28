package com.tecsup.lab10.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class BooksResponse(val items: List<Book> = emptyList())
data class Book(val id: String, val volumeInfo: BookInfo = BookInfo())
data class BookInfo(val title: String = "Sin título", val authors: List<String> = emptyList(), val description: String? = null, val imageLinks: BookImages? = null)
data class BookImages(val thumbnail: String? = null) {
    fun secureThumbnail() = thumbnail?.replace("http://", "https://")
}
interface BooksApiService {
    @GET("volumes") suspend fun search(@Query("q") query: String, @Query("maxResults") limit: Int = 20): BooksResponse
    @GET("volumes/{id}") suspend fun detail(@Path("id") id: String): Book
}
class BooksRepository(private val api: BooksApiService = ApiClient.retrofit("https://www.googleapis.com/books/v1/").create(BooksApiService::class.java)) {
    suspend fun search(query: String) = api.search(query.trim()).items
    suspend fun detail(id: String) = api.detail(id)
}
