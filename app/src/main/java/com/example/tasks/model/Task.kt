package com.example.tasks.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val dateEpochDay: Long,
    val timeMinutes: Int
)

enum class TaskSortOrder(val title: String) {
    DATE_ASCENDING("По дате: сначала ранние"),
    DATE_DESCENDING("По дате: сначала поздние"),
    ALPHABETICAL("По алфавиту")
}

data class UserSettings(
    val selectedDateEpochDay: Long,
    val sortOrder: TaskSortOrder
)
