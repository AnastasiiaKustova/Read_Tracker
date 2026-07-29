package com.example.readtracker.android.data.repository

import com.example.readtracker.android.domain.model.BookDao
import com.example.readtracker.android.domain.repository.LitresRepository
import javax.inject.Inject

class LitresRepositoryImpl @Inject constructor() : LitresRepository {
//    private val client = HttpClient(CIO) {
//        install(ContentNegotiation) {
//            json(kotlinx.serialization.json.Json { ignoreUnknownKeys = true })
//        }
//    }
//
//    suspend fun getGenres(): String {
//        // Публичный эндпоинт для получения дерева жанров Литрес (не требует авторизации)
//        val url = "https://litres.ru"
//
//        return try {
//            val response: HttpResponse = client.get(url) {
//                // uilang определяет язык ответа (опционально)
//                parameter("uilang", "rus")
//            }
//
//            if (response.status == HttpStatusCode.OK) {
//                response.bodyAsText() // Возвращает JSON со списком жанров
//            } else {
//                "Ошибка сервера: ${response.status}"
//            }
//        } catch (e: Exception) {
//            e.localizedMessage ?: "Сетевая ошибка"
//        }
//    }

    override suspend fun getPopularBooks(): List<BookDao> {

        return listOf(
            BookDao(
                id = "1",
                title = "Мастер и Маргарита",
                author = "Михаил Булгаков",
                coverUrl = "https://example.com",
                description = "Культовый роман Михаила Булгакова..."
            ),
            BookDao(
                id = "2",
                title = "Преступление и наказание",
                author = "Федор Достоевский",
                coverUrl = "https://example.com",
                description = "Глубокий философский роман..."
            ),
            BookDao(
                id = "3",
                title = "Понедельник начинается в субботу",
                author = "Аркадий и Борис Стругацкие",
                coverUrl = "https://example.com",
                description = "Фантастическая юмористическая повесть..."
            )
        )
    }
}