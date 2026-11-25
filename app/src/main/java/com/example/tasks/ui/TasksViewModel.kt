package com.example.tasks.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tasks.data.TasksRepository
import com.example.tasks.model.Task
import com.example.tasks.model.TaskSortOrder
import java.time.LocalDate
import java.util.Locale
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TasksUiState(
    val selectedDateEpochDay: Long = LocalDate.now().toEpochDay(),
    val sortOrder: TaskSortOrder = TaskSortOrder.DATE_ASCENDING,
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = true
)

class TasksViewModel(
    private val repository: TasksRepository
) : ViewModel() {
    val uiState: StateFlow<TasksUiState> = combine(
        repository.tasks,
        repository.settings
    ) { tasks, settings ->
        val sortedTasks = tasks
            .filter { it.dateEpochDay == settings.selectedDateEpochDay }
            .sortedWith(settings.sortOrder.comparator)
        TasksUiState(
            selectedDateEpochDay = settings.selectedDateEpochDay,
            sortOrder = settings.sortOrder,
            tasks = sortedTasks,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TasksUiState()
    )

    fun selectDate(dateEpochDay: Long) {
        val today = LocalDate.now().toEpochDay()
        viewModelScope.launch {
            repository.saveSelectedDate(dateEpochDay.coerceAtLeast(today))
        }
    }

    fun showNextDate() = selectDate(uiState.value.selectedDateEpochDay + 1)

    fun showPreviousDate() {
        selectDate(uiState.value.selectedDateEpochDay - 1)
    }

    fun setSortOrder(sortOrder: TaskSortOrder) {
        viewModelScope.launch { repository.saveSortOrder(sortOrder) }
    }

    fun saveTask(
        id: Long,
        title: String,
        description: String,
        dateEpochDay: Long,
        timeMinutes: Int
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        val task = Task(
            id = id,
            title = cleanTitle,
            description = description.trim(),
            dateEpochDay = dateEpochDay,
            timeMinutes = timeMinutes.coerceIn(0, 23 * 60 + 59)
        )
        viewModelScope.launch {
            if (id == 0L) repository.addTask(task) else repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    class Factory(
        private val repository: TasksRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(TasksViewModel::class.java))
            return TasksViewModel(repository) as T
        }
    }
}

private val TaskSortOrder.comparator: Comparator<Task>
    get() = when (this) {
        TaskSortOrder.DATE_ASCENDING -> compareBy<Task> { it.dateEpochDay }
            .thenBy { it.timeMinutes }
            .thenBy { it.title.lowercase(Locale.getDefault()) }

        TaskSortOrder.DATE_DESCENDING -> compareByDescending<Task> { it.dateEpochDay }
            .thenByDescending { it.timeMinutes }
            .thenBy { it.title.lowercase(Locale.getDefault()) }

        TaskSortOrder.ALPHABETICAL -> compareBy<Task> {
            it.title.lowercase(Locale.getDefault())
        }.thenBy { it.dateEpochDay }.thenBy { it.timeMinutes }
    }
