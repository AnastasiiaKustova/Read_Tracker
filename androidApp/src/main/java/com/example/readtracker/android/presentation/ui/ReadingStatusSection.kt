package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.BookStatus

@Composable
fun ReadingStatusSection(
    onStatusClick: (BookStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)) {
        Text(
            text = "По статусу чтения",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(195.dp)
        ) {
            // Автоматически берет все элементы Enum: READING, FINISHED, ON_HOLD, DROPPED
            items(BookStatus.entries) { status ->
                StatusButton(
                    status = status,
                    onClick = onStatusClick
                )
            }
        }
    }
}

@Preview
@Composable
fun TestReadingStatusSection(){
    ReadingStatusSection(
        onStatusClick = {},
    )
}