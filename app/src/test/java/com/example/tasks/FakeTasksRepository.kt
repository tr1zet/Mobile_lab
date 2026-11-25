package com.example.tasks

import com.example.tasks.data.TasksRepository
import com.example.tasks.model.Task
import com.example.tasks.model.TaskSortOrder
import com.example.tasks.model.UserSettings
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTasksRepository : TasksRepository {
    val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    val settingsFlow = MutableStateFlow(
        UserSettings(
            selectedDateEpochDay = LocalDate.now().toEpochDay(),
            sortOrder = TaskSortOrder.DATE_ASCENDING
        )
    )

    override val tasks: Flow<List<Task>> = tasksFlow
    override val settings: Flow<UserSettings> = settingsFlow

    override suspend fun addTask(task: Task) {
        val nextId = (tasksFlow.value.maxOfOrNull { it.id } ?: 0) + 1
        tasksFlow.value = tasksFlow.value + task.copy(id = nextId)
    }

    override suspend fun updateTask(task: Task) {
        tasksFlow.value = tasksFlow.value.map { if (it.id == task.id) task else it }
    }

    override suspend fun deleteTask(task: Task) {
        tasksFlow.value = tasksFlow.value.filterNot { it.id == task.id }
    }

    override suspend fun saveSelectedDate(dateEpochDay: Long) {
        settingsFlow.value = settingsFlow.value.copy(selectedDateEpochDay = dateEpochDay)
    }

    override suspend fun saveSortOrder(sortOrder: TaskSortOrder) {
        settingsFlow.value = settingsFlow.value.copy(sortOrder = sortOrder)
    }
}

fun task(
    id: Long,
    title: String,
    dateEpochDay: Long = LocalDate.now().toEpochDay(),
    timeMinutes: Int = 12 * 60
) = Task(
    id = id,
    title = title,
    description = "Описание",
    dateEpochDay = dateEpochDay,
    timeMinutes = timeMinutes
)
