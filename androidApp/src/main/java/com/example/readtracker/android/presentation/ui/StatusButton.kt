package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.BookStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusButton(
    status: BookStatus,
    onClick: (BookStatus) -> Unit, // Передаем кликнутый статус обратно
    modifier: Modifier = Modifier
) {
    Card(
        onClick = { onClick(status) },
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE5E5E5)
        )
    ) {
        Box(modifier = Modifier.fillMaxSize().clipToBounds()) {
            Text(
                text = status.title, // Берем текст из Enum
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp)
                    .align(Alignment.TopStart)
            )

            Icon(
                imageVector = status.icon, // Берем иконку из Enum
                contentDescription = null,
                tint = Color(0xFF9E9E9E),
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 15.dp)
                    .rotate(15f)
            )
        }
    }
}