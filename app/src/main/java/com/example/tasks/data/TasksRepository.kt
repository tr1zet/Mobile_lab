package com.example.tasks.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.tasks.data.local.TaskDao
import com.example.tasks.data.local.toDomain
import com.example.tasks.data.local.toEntity
import com.example.tasks.model.Task
import com.example.tasks.model.TaskSortOrder
import com.example.tasks.model.UserSettings
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface TasksRepository {
    val tasks: Flow<List<Task>>
    val settings: Flow<UserSettings>

    suspend fun addTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun saveSelectedDate(dateEpochDay: Long)
    suspend fun saveSortOrder(sortOrder: TaskSortOrder)
}

class OfflineTasksRepository(
    private val taskDao: TaskDao,
    private val dataStore: DataStore<Preferences>
) : TasksRepository {
    override val tasks: Flow<List<Task>> = taskDao.observeAllTasks()
        .map { entities -> entities.map { it.toDomain() } }

    override val settings: Flow<UserSettings> = dataStore.data.map { preferences ->
        val today = LocalDate.now().toEpochDay()
        val storedDate = preferences[SELECTED_DATE_KEY] ?: today
        UserSettings(
            selectedDateEpochDay = storedDate.coerceAtLeast(today),
            sortOrder = preferences[SORT_ORDER_KEY]
                ?.let { stored -> TaskSortOrder.entries.firstOrNull { it.name == stored } }
                ?: TaskSortOrder.DATE_ASCENDING
        )
    }

    override suspend fun addTask(task: Task) {
        taskDao.insert(task.copy(id = 0).toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.toEntity())
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.toEntity())
    }

    override suspend fun saveSelectedDate(dateEpochDay: Long) {
        dataStore.edit { preferences -> preferences[SELECTED_DATE_KEY] = dateEpochDay }
    }

    override suspend fun saveSortOrder(sortOrder: TaskSortOrder) {
        dataStore.edit { preferences -> preferences[SORT_ORDER_KEY] = sortOrder.name }
    }

    private companion object {
        val SELECTED_DATE_KEY = longPreferencesKey("selected_date_epoch_day")
        val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
    }
}
