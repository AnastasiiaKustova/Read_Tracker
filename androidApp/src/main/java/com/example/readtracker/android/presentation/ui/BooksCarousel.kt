package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readtracker.android.domain.entity.Book
import com.example.readtracker.android.domain.entity.BookStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BooksCarousel(bookSet: Set<Book>, onBookClick: (String) -> Unit, modifier: Modifier = Modifier) {

    val pagerState = rememberPagerState(pageCount = { bookSet.size })

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxWidth(),
        //contentPadding задает отступы по бокам, чтобы виднелись соседние карточки
        contentPadding = PaddingValues(horizontal = 20.dp),
        // pageSpacing задает расстояние между самими карточками
        pageSpacing = 12.dp
    ) { page ->
        val book = bookSet.elementAt(page)

        BookCard(
            title = book.title,
            author = book.author,
            currentPage = book.currentPage,
            totalPages = book.totalPages,
            onCardClick = { onBookClick(book.id) }
        )
    }
}

@Preview
@Composable
fun BooksCarouselTest(){
    // Тестовый список данных
    val dummyBooks = setOf(
        Book("0","Название в две строчки или может в три и вс...", "Автор Такойто", "",12345, 15456, 0, BookStatus.READING),
        Book("1","Мастер и Маргарита", "Михаил Булгаков", "",200, 450, 0, BookStatus.READING),
        Book("2","Преступление и наказание", "Федор Достоевский", "",50, 600, 0, BookStatus.READING)
    )

    BooksCarousel(dummyBooks, {})
}