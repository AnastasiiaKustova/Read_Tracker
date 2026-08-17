package com.example.readtracker.android.data.repository

import com.example.readtracker.android.domain.entity.database.BookItem
import com.example.readtracker.android.domain.entity.database.CategoryItem
import com.example.readtracker.android.domain.repository.LitresRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LitresRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : LitresRepository {


    override suspend fun searchBooks(query: String): Set<BookItem> {
        return withContext(Dispatchers.IO) {
            try {
                if (query.isBlank()) return@withContext emptySet()

                val rawBooks = supabase.from("books")
                    .select {
                        filter {
                            or {
                                ilike("name", "%$query%")
                                ilike("author", "%$query%")
                            }
                        }
                        range(0, 19)
                    }
                    .decodeList<BookItem>()

                // Сортируем и убираем дубликаты прямо в фоне
                rawBooks.distinctBy { it.id }
                    .distinctBy { it.title }
                    .sortedBy { it.title }
                    .toSet()
            } catch (e: Exception) {
                e.printStackTrace()
                emptySet()
            }
        }
    }

    override suspend fun searchCategories(ids: String): List<CategoryItem> {
        return withContext(Dispatchers.IO) {
            try {
                if (ids.isBlank()) return@withContext emptyList()

                // 1. Разбиваем строку по запятым, убираем лишние пробелы и пустые элементы
                // "5267,5223,5225" -> ["5267", "5223", "5225"]
                val idsList: List<Long> = ids.split(",")
                    .map { it.trim().toLong() }

                if (idsList.isEmpty()) return@withContext emptyList()

                // 2. Делаем запрос к Supabase к таблице категорий (например, "categories")
                val rawCategories = supabase.from("categories")
                    .select {
                        filter {
                            isIn("_id", idsList)
                        }
                    }
                    .decodeList<CategoryItem>() // Декодируем в список категорий

                // 3. Превращаем в Set, убирая возможные дубликаты и сортируя по названию
                rawCategories.distinctBy { it.id }
                    //.sortedBy { it.title }

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // В случае сбоя сети возвращаем пустое множество, чтобы не крашить приложение
            }
        }
    }
}