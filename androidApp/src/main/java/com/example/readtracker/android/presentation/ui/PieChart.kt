package com.example.readtracker.android.presentation.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readtracker.android.domain.entity.stats.ReadStat
import kotlin.random.Random

data class PieChartData(
    val categoryName: String,
    val value: Float, // Количество книг (например: 3f, 5f, 10f)
    val color: Color
)

fun generateRandomColor(): Color {
    return Color(
        red = Random.nextInt(100, 220) / 255f,   // Избегаем слишком темных
        green = Random.nextInt(100, 220) / 255f, // и слишком ярких кислотных цветов
        blue = Random.nextInt(100, 220) / 255f,
        alpha = 1.0f
    )
}

fun prepareStatsForPieChart(
    statsList: List<ReadStat>,
    selector: (ReadStat) -> String
): List<PieChartData> {
    return statsList
        // 1. Группируем по переданному правилу
        .groupBy(selector)
        // 2. Превращаем в промежуточный список, отсекая пустые группы
        .mapNotNull { (categoryName, sessions) ->
            val totalPagesRead = sessions.sumOf { it.pagesRead }.toFloat()
            if (totalPagesRead <= 0f) return@mapNotNull null

            // Временно сохраняем имя и значение
            categoryName to totalPagesRead
        }
        // 3. Назначаем цвета по порядку из нашей палитры
        .mapIndexed { index, (categoryName, totalPagesRead) ->
            // Оператор % гарантирует, что если индекс будет 21,
            // мы безопасно возьмем 1-й цвет из списка (21 % 20 = 1)
            val colorFromPalette = AppPalette.chartColors[index % AppPalette.chartColors.size]

            PieChartData(
                categoryName = categoryName,
                value = totalPagesRead,
                color = colorFromPalette
            )
        }
}

@Composable
fun PieChart(
    dataList: List<PieChartData>,
    modifier: Modifier = Modifier
) {
    // 1. Считаем общее количество книг во всех категориях
    val totalSum = dataList.sumOf { it.value.toDouble() }.toFloat()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 2. САМ ПИРОГ (Отрисовка через Canvas)
        Canvas(
            modifier = Modifier
                .size(140.dp) // Размер самого круга диаграммы
        ) {
            // Если данных нет, рисуем пустой серый круг
            if (totalSum == 0f) {
                drawArc(
                    color = Color.LightGray,
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )
            } else {
                var currentStartAngle = -90f // Начинаем рисовать сверху (12 часов)

                dataList.forEach { item ->
                    // Вычисляем, какой угол в градусах занимает эта категория
                    val sweepAngle = (item.value / totalSum) * 360f

                    drawArc(
                        color = item.color,
                        startAngle = currentStartAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true, // Заполняем до центра (получается пирог)
                        size = Size(size.width, size.height)
                    )
                    // Сдвигаем стартовый угол для следующего сектора
                    currentStartAngle += sweepAngle
                }
            }
        }

        // 3. ЛЕГЕНДА СПРАВА (Список категорий с их процентами)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            dataList.forEach { item ->
                // Считаем процент для вывода текста
                val percentage = if (totalSum > 0f) (item.value / totalSum * 100).toInt() else 0

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Цветной кружочек-индикатор
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(item.color, CircleShape)
                    )
                    // Текст: Название (Количество) — Проценты
                    Text(
                        text = "${item.categoryName} (${item.value.toInt()})",
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$percentage%",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun TestPieChart(){
    val genreStats = listOf(
        PieChartData(categoryName = "Классика", value = 3f, color = Color(0xFF8BAE91)),   // Светло-зеленый
        PieChartData(categoryName = "Фантастика", value = 5f, color = Color(0xFF26A6D1)), // Синий
        PieChartData(categoryName = "Комиксы", value = 10f, color = Color(0xFFFFA000))    // Оранжевый
    )

    PieChart(dataList = genreStats)
}