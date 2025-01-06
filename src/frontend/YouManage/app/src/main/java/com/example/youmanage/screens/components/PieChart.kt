package com.example.youmanage.screens.components

import android.annotation.SuppressLint
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


val pieChartInput = listOf(
    PieChartInput(
        color = Color(0xffbaf4ca),
        value = 50.0,
        description = "Done"
    ),
    PieChartInput(
        color = Color(0xfffccdcd),
        value = 24.95,
        description = "In Progress"
    ),
    PieChartInput(
        color = Color(0xffedf0f2),
        value = 25.0,
        description = "To Do"
    ),
)

@SuppressLint("DefaultLocale")
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    size: Dp = 300.dp,
    input: List<PieChartInput>
) {
    var circleCenter by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Overall Activity",
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        val semiCircleColor = MaterialTheme.colorScheme.primaryContainer
        var donePercentColor = MaterialTheme.colorScheme.primary
        val textColor = MaterialTheme.colorScheme.onTertiary

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            modifier = Modifier
                .width(size)
                .aspectRatio(2f)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val width = this.size.width
                val height = this.size.height
                val radius = width / 2
                val innerRadius = radius * 0.85f

                circleCenter = Offset(x = width / 2f, y = height)

                val totalValue = input.sumOf { it.value }
                val anglePerValue = -180f / totalValue
                var currentStartAngle = 0f

                input.reversed().forEach { pieChartInput ->
                    val angleToDraw = pieChartInput.value * anglePerValue
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw.toFloat(),
                        useCenter = true,
                        size = Size(width = radius * 2f, height = radius * 2f),
                        topLeft = Offset(0f, 0f)
                    )
                    currentStartAngle += angleToDraw.toFloat()
                }

                drawArc(
                    color = semiCircleColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    size = Size(width = innerRadius * 2f, height = innerRadius * 2f),
                    topLeft = Offset(
                        (width - innerRadius * 2f) / 2f,
                        (width - innerRadius * 2f) / 2f
                    )
                )

                drawContext.canvas.nativeCanvas.apply {
                    val mainTextSize = innerRadius * 0.25f
                    val subTextSize = innerRadius * 0.17f

                    val textMainPaint = Paint().apply {
                        color = textColor.toArgb()
                        textSize = mainTextSize
                        isFakeBoldText = true
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    val textPaint = Paint().apply {
                        color = textColor.toArgb()
                        textSize = subTextSize
                        isFakeBoldText = true
                        textAlign = Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    val text = if (input.isNotEmpty()) {
                        try {
                            val value = input.last().value.toString().replace(",", ".")
                            "${String.format("%.1f", value.toDouble())}%"
                        } catch (e: NumberFormatException) {
                            "Invalid Value"
                        }
                    } else {
                        "Null"
                    }

                    val textBounds = android.graphics.Rect()
                    textPaint.getTextBounds(text, 0, text.length, textBounds)

                    val textY = height - radius / 2
                    drawText(
                        text,
                        width / 2f + 15,
                        textY + 60,
                        textMainPaint
                    )

                    val textBelow = "Done"
                    val textBelowY = textY + textBounds.height() + (innerRadius * 0.35f)
                    drawText(
                        textBelow,
                        width / 2f,
                        textBelowY,
                        textPaint
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            input.forEach { item ->
                AnnotationItem(item.color, item.description)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun AnnotationItem(
    color: Color,
    name: String,
    textSize: TextUnit = 15.sp
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(15.dp)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = textSize,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview
@Composable
fun OverallSection(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        PieChart(input = pieChartInput)
    }

}

data class PieChartInput(
    val color: Color,
    val value: Double,
    val description: String
)