package com.example.tasks.ui

import com.example.tasks.FakeTasksRepository
import com.example.tasks.MainDispatcherRule
import com.example.tasks.model.TaskSortOrder
import com.example.tasks.task
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TasksViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun tasksForSelectedDate_areSortedByTimeAscending() =
        runTest(mainDispatcherRule.testDispatcher) {
            val today = LocalDate.now().toEpochDay()
            val repository = FakeTasksRepository().apply {
                tasksFlow.value = listOf(
                    task(id = 1, title = "Поздняя", timeMinutes = 18 * 60),
                    task(id = 2, title = "Завтрашняя", dateEpochDay = today + 1),
                    task(id = 3, title = "Ранняя", timeMinutes = 8 * 60)
                )
            }

            val viewModel = TasksViewModel(repository)
            advanceUntilIdle()

            assertEquals(listOf("Ранняя", "Поздняя"), viewModel.uiState.value.tasks.map { it.title })
        }

    @Test
    fun selectPastDate_clampsSelectionToToday() =
        runTest(mainDispatcherRule.testDispatcher) {
            val today = LocalDate.now().toEpochDay()
            val repository = FakeTasksRepository()
            val viewModel = TasksViewModel(repository)

            viewModel.selectDate(today - 10)
            advanceUntilIdle()

            assertEquals(today, repository.settingsFlow.value.selectedDateEpochDay)
        }

    @Test
    fun alphabeticSort_isSavedAndApplied() =
        runTest(mainDispatcherRule.testDispatcher) {
            val repository = FakeTasksRepository().apply {
                tasksFlow.value = listOf(
                    task(id = 1, title = "Яблоко"),
                    task(id = 2, title = "Апельсин")
                )
            }
            val viewModel = TasksViewModel(repository)

            viewModel.setSortOrder(TaskSortOrder.ALPHABETICAL)
            advanceUntilIdle()

            assertEquals(TaskSortOrder.ALPHABETICAL, repository.settingsFlow.value.sortOrder)
            assertEquals(listOf("Апельсин", "Яблоко"), viewModel.uiState.value.tasks.map { it.title })
        }

    @Test
    fun createEditAndDeleteTask_updatesRepository() =
        runTest(mainDispatcherRule.testDispatcher) {
            val today = LocalDate.now().toEpochDay()
            val repository = FakeTasksRepository()
            val viewModel = TasksViewModel(repository)

            viewModel.saveTask(0, "Новая", "Описание", today, 600)
            advanceUntilIdle()
            val created = repository.tasksFlow.value.single()
            assertTrue(created.id > 0)

            viewModel.saveTask(created.id, "Изменённая", "", today, 700)
            advanceUntilIdle()
            val updated = repository.tasksFlow.value.single()
            assertEquals("Изменённая", updated.title)
            assertEquals(700, updated.timeMinutes)

            viewModel.deleteTask(updated)
            advanceUntilIdle()
            assertTrue(repository.tasksFlow.value.isEmpty())
        }
}
