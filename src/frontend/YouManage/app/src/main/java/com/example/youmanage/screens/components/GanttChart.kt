package com.example.youmanage.screens.components

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.youmanage.utils.ProjectTimeline
import com.example.youmanage.utils.randomVibrantLightColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Paint as TextPaint

@Composable
fun GanttChart(
    projectTimeLine: ProjectTimeline,
    timelineBackgroundColor: Color = Color(0xFF00B7FF),
    timelineBorderColor: Color = Color.White,
    timelineTextColor: Color = Color.White,
    ganttBarColor: Color = Color(0xffBAE5F5),
    ganttBarLabelColor: Color = MaterialTheme.colorScheme.primary
) {
    val dateFormatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    val totalDays = ((projectTimeLine.projectEndDate.time - projectTimeLine.projectStartDate.time) / (1000 * 60 * 60 * 24)).toInt()
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Pair<String, Pair<String, String>?>>(Pair("", null)) }

    Row(modifier = Modifier.horizontalScroll(scrollState)) {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(start = 20.dp)
        ) {
            // Timeline
            Canvas(modifier = Modifier
                .width(50.dp * totalDays)
                .height(40.dp)) {
                drawTimeline(
                    backgroundColor = timelineBackgroundColor,
                    borderColor = timelineBorderColor,
                    textColor = timelineTextColor,
                    totalDays = totalDays,
                    startDate = projectTimeLine.projectStartDate,
                    dateFormatter = dateFormatter
                )
            }

            // Task List
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(projectTimeLine.chartData.size) { index ->
                    val task = projectTimeLine.chartData[index]
                    val chartWidth = 50.dp.value * totalDays
                    val taskStartX = chartWidth * task.start + 150f
                    val taskEndX = chartWidth * task.end + 150f
                    Canvas(
                        modifier = Modifier
                            .width(50.dp * totalDays)
                            .weight(2f)
                            .height(40.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onTap = {
                                        Log.i("GanttChart", "Task tapped: ${task.title}")
                                        selectedTask = Pair(task.title, Pair(task.startTime, task.endTime))
                                        showDialog = true
                                    }
                                )
                            }
                    ) {
                        drawGanttBar(
                            start = task.start,
                            end = task.end,
                            label = task.title,
                            barColor = ganttBarColor,
                            labelColor = ganttBarLabelColor
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismiss = { showDialog = false },
            title = selectedTask.first,
            content = "Start: ${selectedTask.second?.first}\nEnd: ${selectedTask.second?.second}",
            showDialog = showDialog,
            onConfirm = { showDialog = false }
        )
    }
}

private fun DrawScope.drawTimeline(
    backgroundColor: Color,
    borderColor: Color,
    textColor: Color,
    totalDays: Int,
    startDate: Date,
    dateFormatter: SimpleDateFormat
) {
    val dayWidth = size.width / totalDays
    val headerHeight = 100f

    val textPaint = TextPaint().apply {
        color = android.graphics.Color.WHITE
        textSize = 36f
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    val titlePaint = TextPaint().apply {
        color = textColor.toArgb()
        textSize = 42f
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    // Draw background for the header
    drawRect(
        color = backgroundColor,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, headerHeight)
    )

    // Draw border for the header
    drawRect(
        color = borderColor,
        topLeft = Offset(0f, 0f),
        size = Size(size.width, headerHeight),
        style = Stroke(width = 4f)
    )

    // Draw the title
    drawContext.canvas.nativeCanvas.drawText(
        "Task",
        10f,
        headerHeight / 2f + 10,
        titlePaint
    )

    for (i in 0 until totalDays) {
        val x = i * dayWidth + 150f
        val dayDate = Date(startDate.time + i * 24L * 60L * 60L * 1000L)

        // Draw vertical lines for each day
        drawLine(
            color = borderColor,
            start = Offset(x, 0f),
            end = Offset(x, headerHeight),
            strokeWidth = 2f
        )

        // Draw the date text
        drawContext.canvas.nativeCanvas.drawText(
            dateFormatter.format(dayDate),
            x + 20,
            headerHeight / 2f + 10,
            textPaint
        )
    }
}

private fun DrawScope.drawGanttBar(
    start: Float,
    end: Float,
    label: String,
    barColor: Color,
    labelColor: Color
) {
    val chartWidth = size.width
    val barHeight = size.height * 0.8f

    val taskStartX = chartWidth * start + 150f
    val taskEndX = chartWidth * end + 150f

    // Draw the Gantt bar
    drawRoundRect(
        color = barColor,
        topLeft = Offset(taskStartX, (size.height - barHeight) / 2),
        size = Size(taskEndX - taskStartX, barHeight),
        cornerRadius = CornerRadius(barHeight / 5, barHeight / 5)
    )

    // Draw the task label
    val textPaint = TextPaint().apply {
        color = labelColor.toArgb()
        textSize = 32f
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    val displayedLabel = if (label.length > 8) label.take(6) + "..." else label
    val labelX = 0f
    val labelY = (size.height - barHeight) / 2 + barHeight / 2 + 10
    drawContext.canvas.nativeCanvas.drawText(
        displayedLabel,
        labelX,
        labelY,
        textPaint
    )
}
