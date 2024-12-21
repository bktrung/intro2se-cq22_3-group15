package com.example.youmanage.utils

import com.example.youmanage.data.remote.projectmanagement.Project
import com.example.youmanage.data.remote.taskmanagement.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}

fun parseDate(dateString: String): Date? {
    return try {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        formatter.parse(dateString)
    } catch (e: Exception) {
        null
    }
}

fun calculateTaskDuration(startDate: Date, endDate: Date): Long {
    return (endDate.time - startDate.time) / (1000 * 60 * 60 * 24) // Duration in days
}

fun generateChartData(tasks: List<Task>, project: Project): ProjectTimeline {
    if (tasks.isEmpty()) return ProjectTimeline(Date(), Date(), emptyList())

    val dateTasks = tasks.mapNotNull { task ->
        val start = parseDate(task.startDate)
        val end = parseDate(task.endDate)
        if (start != null && end != null) Pair(task, start to end) else null
    }

    if (tasks.isEmpty()) return ProjectTimeline(Date(), Date(), emptyList())

    val projectStartDate = dateTasks.minOfOrNull { it.second.first } ?: Date()
    val projectEndDate = parseDate(project.dueDate) ?: Date()
    val projectDuration = calculateTaskDuration(projectStartDate, projectEndDate).toFloat()

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

   val chartData = dateTasks.map { (task, dateRange) ->
        val (taskStart, taskEnd) = dateRange
        val taskStartNormalized = (taskStart.time - projectStartDate.time).toFloat() / (1000 * 60 * 60 * 24) / projectDuration
       val taskEndNormalized = ((taskEnd.time + 24L * 60L * 60L * 1000L) - projectStartDate.time).toFloat() / (1000 * 60 * 60 * 24) / projectDuration
        ChartData(
            title = task.title,
            start = taskStartNormalized,
            end = taskEndNormalized,
            startTime = dateFormatter.format(taskStart),
            endTime = dateFormatter.format(taskEnd)
        )
    }
    return ProjectTimeline(projectStartDate, projectEndDate, chartData)
}



data class ChartData(
    val title: String,
    val start: Float,
    val end: Float,
    val startTime: String,
    val endTime: String
)
data class ProjectTimeline(
    val projectStartDate: Date,
    val projectEndDate: Date,
    val chartData: List<ChartData>
)