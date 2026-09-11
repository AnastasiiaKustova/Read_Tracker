package com.example.readtracker.android.data.mapper

import android.net.Uri
import com.example.readtracker.android.domain.entity.note.Note
import com.example.readtracker.android.domain.entity.note.NoteEntity
import com.example.readtracker.android.domain.entity.book.Book
import com.example.readtracker.android.domain.entity.bookCollection.BookCollection
import com.example.readtracker.android.domain.entity.bookCollection.BookCollectionEntity
import com.example.readtracker.android.domain.entity.bookCollection.BookCollectionWithBooksEntity
import com.example.readtracker.android.domain.entity.book.BookEntity
import com.example.readtracker.android.domain.entity.BookStatus
import com.example.readtracker.android.domain.entity.note.NoteWithTagsEntity
import com.example.readtracker.android.domain.entity.stats.ReadStat
import com.example.readtracker.android.domain.entity.stats.StatsEntity
import com.example.readtracker.android.domain.entity.stats.StatsWithBookEntity
import com.example.readtracker.android.domain.entity.tag.Tag
import com.example.readtracker.android.domain.entity.tag.TagEntity

// --- ИЗ ДОМЕНА В БАЗУ ДАННЫХ (Одиночный объект) ---
@JvmName("bookToBookEntity")
fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        author = this.author,
        description = this.description,
        coverUriString = this.coverUri?.toString(), // Uri превращаем в String
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        rating = this.rating,
        series = this.series,
        idLitres = this.idLitres,
        bookStatusString = this.bookStatus.name // Enum превращаем в String ("FINISHED", "READING" и т.д.)
    )
}

@JvmName("bookEntityToBook")
// --- ИЗ БАЗЫ ДАННЫХ В ДОМЕН (Одиночный объект) ---
fun BookEntity.toDomain(): Book {
    return Book(
        id = this.id,
        title = this.title,
        author = this.author,
        description = this.description,
        // Восстанавливаем Uri из строки (если она была)
        coverUri = this.coverUriString?.let { Uri.parse(it) },
        totalPages = this.totalPages,
        currentPage = this.currentPage,
        rating = this.rating,
        series = this.series,
        idLitres = this.idLitres,
        // Восстанавливаем Enum из строки безопасно, с резервным статусом
        bookStatus = try {
            BookStatus.valueOf(this.bookStatusString)
        } catch (e: Exception) {
            BookStatus.READING
        }
    )
}

// --- МАППЕРЫ ДЛЯ КОЛЛЕКЦИЙ (Списки и Множества) ---

// Из List<BookEntity> (что возвращает Room) в ваш Set<Book> для UI
@JvmName("bookListToBookSet")
fun List<BookEntity>.toDomainSet(): Set<Book> {
    return this.map { it.toDomain() }.toSet()
}

// Из вашего Set<Book> в List<BookEntity> для сохранения пачкой в Room
@JvmName("bookSetToBookList")
fun Set<Book>.toEntityList(): List<BookEntity> {
    return this.map { it.toEntity() }
}

// --- ИЗ ДОМЕНА В БАЗУ ДАННЫХ (Одиночный объект) ---
@JvmName("noteToNoteEntity")
fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = this.id,
        quoteText = this.quoteText,
        pageNumber = this.pageNumber,
        userComment = this.userComment,
        bookId = this.book.id, // Берём чистый ID книги для связки таблиц
        isPublic = this.isPublic,
        createdAt = this.createdAt
    )
}

// --- ИЗ БАЗЫ ДАННЫХ В ДОМЕН (Одиночный объект) ---
@JvmName("noteWithTagsEntityToNote")
fun NoteWithTagsEntity.toDomain(): Note {
    return Note(
        id = this.note.id,
        quoteText = this.note.quoteText,
        pageNumber = this.note.pageNumber,
        userComment = this.note.userComment,
        book = this.book.toDomain(),
        tags = this.tags.map { tagEntity ->
            Tag(id = tagEntity.id, title = tagEntity.title)
        }.toSet() ,
        isPublic = this.note.isPublic,
        createdAt = this.note.createdAt
    )
}

@JvmName("noteToListTagEntity")
fun Note.toTagEntities(): List<TagEntity> {
    return this.tags.map { tag ->
        TagEntity(id = tag.id, title = tag.title)
    }
}

// --- МАППЕРЫ ДЛЯ КОЛЛЕКЦИЙ (Списки и Множества) ---

// Из List<NoteEntity> (что возвращает Room) в ваш Set<Note> для UI
@JvmName("listNoteToSetNote")
fun List<NoteWithTagsEntity>.toDomainSet(): Set<Note> {
    return this.map { it.toDomain() }.toSet()
}

// Из вашего Set<Note> в обычный плоский List<NoteEntity> (если нужно сохранить пачкой без тегов)
@JvmName("setNoteToListNote")
fun Set<Note>.toEntityList(): List<NoteEntity> {
    return this.map { it.toEntity() }
}

// --- ИЗ ДОМЕНА В БАЗУ ДАННЫХ (Одиночный объект) ---
@JvmName("tagToTagEntity")
fun Tag.toEntity(): TagEntity {
    return TagEntity(
        id = this.id,
        title = this.title,
    )
}

// --- ИЗ БАЗЫ ДАННЫХ В ДОМЕН (Одиночный объект) ---
@JvmName("tagEntityToTag")
fun TagEntity.toDomain(): Tag {
    return Tag(
        id = this.id,
        title = this.title,
    )
}

// --- МАППЕРЫ ДЛЯ КОЛЛЕКЦИЙ (Списки и Множества) ---

// Из List<TagEntity> (что возвращает Room) в ваш Set<Tag> для UI
@JvmName("listTagEntityToSetTag")
fun List<TagEntity>.toDomainSet(): Set<Tag> {
    return this.map { it.toDomain() }.toSet()
}

// Из вашего Set<Tag> в List<TagEntity> для сохранения пачкой в Room
@JvmName("setTagToListTagEntity")
fun Set<Tag>.toEntityList(): List<TagEntity> {
    return this.map { it.toEntity() }
}

// --- ИЗ ДОМЕНА В БАЗУ ДАННЫХ (Одиночный объект) ---
@JvmName("bookCollectionToBookCollectionEntity")
fun BookCollection.toEntity(): BookCollectionEntity {
    return BookCollectionEntity(
        id = this.id,
        title = this.title
    )
}

// --- ИЗ БАЗЫ ДАННЫХ В ДОМЕН (Одиночный объект) ---
@JvmName("bookCollectionWithBooksEntityToBookCollection")
fun BookCollectionWithBooksEntity.toDomain(): BookCollection {
    return BookCollection(
        id = this.collection.id,
        title = this.collection.title,
        books = this.books.map { bookEntity ->
            bookEntity.toDomain()
        }.toSet()
    )
}

@JvmName("bookCollectionToBookEntity")
fun BookCollection.toBookEntities(): List<BookEntity> {
    return this.books.map { book ->
        book.toEntity()
    }
}

// --- МАППЕРЫ ДЛЯ КОЛЛЕКЦИЙ (Списки и Множества) ---

// Из List<BookCollectionEntity> (что возвращает Room) в ваш Set<BookCollection> для UI
@JvmName("listBookCollectionWithBooksEntityToSetBookCollection")
fun List<BookCollectionWithBooksEntity>.toDomainSet(): Set<BookCollection> {
    return this.map { it.toDomain() }.toSet()
}

// Из вашего Set<BookCollection> в обычный плоский List<BookCollectionEntity> (если нужно сохранить пачкой без тегов)
@JvmName("setBookCollectionToListBookCollectionEntity")
fun Set<BookCollection>.toEntityList(): List<BookCollectionEntity> {
    return this.map { it.toEntity() }
}

@JvmName("readStatToStatsEntity")
fun ReadStat.toEntity(): StatsEntity {
    return StatsEntity(
        id = this.id,
        timestamp = this.timestamp,
        bookId = this.book.id, // Вытаскиваем ID книги для внешнего ключа (foreignKey)
        pagesRead = this.pagesRead,
        durationMinutes = this.durationMinutes,
        statusChangedTo = this.statusChangedTo?.name // Маппим Enum статуса в String? ("COMPLETED", "READING" и т.д.)
    )
}

@JvmName("statsWithBookToDomain")
fun StatsWithBookEntity.toDomain(): ReadStat {
    return ReadStat(
        id = this.stats.id,
        timestamp = this.stats.timestamp,
        book = this.book.toDomain(), // Используем ваш стандартный маппер книги BookEntity.toDomain()
        pagesRead = this.stats.pagesRead,
        durationMinutes = this.stats.durationMinutes,
        statusChangedTo = this.stats.statusChangedTo?.let {
            try { BookStatus.valueOf(it) } catch (e: Exception) { null }
        }
    )
}

// Маппер для списков
@JvmName("statsWithBookListToDomainList")
fun List<StatsWithBookEntity>.toDomainList(): List<ReadStat> {
    return this.map { it.toDomain() }
}

// --- 3. МАППЕРЫ ДЛЯ КОЛЛЕКЦИЙ ---

// Из List<ReadStat> в List<StatsEntity> (для сохранения пачкой/списком в Room)
@JvmName("readStatListToStatsEntityList")
fun List<ReadStat>.toEntityList(): List<StatsEntity> {
    return this.map { it.toEntity() }
}