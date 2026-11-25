package com.example.tasks.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasks.data.TasksRepository
import com.example.tasks.model.Task
import com.example.tasks.model.TaskSortOrder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TasksApp(repository: TasksRepository) {
    val viewModel: TasksViewModel = viewModel(factory = TasksViewModel.Factory(repository))
    TasksScreen(viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TasksScreen(viewModel: TasksViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var editorTask by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var showCalendar by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Задачи", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        TextButton(onClick = { showSortMenu = true }) {
                            Text("Сортировка ▾")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            TaskSortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = if (order == uiState.sortOrder) {
                                                "✓ ${order.title}"
                                            } else {
                                                order.title
                                            }
                                        )
                                    },
                                    onClick = {
                                        viewModel.setSortOrder(order)
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editorTask = Task(
                        title = "",
                        description = "",
                        dateEpochDay = uiState.selectedDateEpochDay,
                        timeMinutes = currentTimeMinutes()
                    )
                }
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            DateNavigation(
                selectedDateEpochDay = uiState.selectedDateEpochDay,
                onPrevious = viewModel::showPreviousDate,
                onNext = viewModel::showNextDate,
                onCalendar = { showCalendar = true }
            )

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                uiState.tasks.isEmpty() -> EmptyTasks(modifier = Modifier.fillMaxSize())

                else -> TaskList(
                    tasks = uiState.tasks,
                    onEdit = { editorTask = it },
                    onDelete = { taskToDelete = it },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    if (showCalendar) {
        TaskDatePickerDialog(
            initialDateEpochDay = uiState.selectedDateEpochDay,
            onDismiss = { showCalendar = false },
            onDateSelected = { date ->
                viewModel.selectDate(date)
                showCalendar = false
            }
        )
    }

    editorTask?.let { task ->
        TaskEditorDialog(
            initialTask = task,
            onDismiss = { editorTask = null },
            onSave = { saved ->
                viewModel.saveTask(
                    id = saved.id,
                    title = saved.title,
                    description = saved.description,
                    dateEpochDay = saved.dateEpochDay,
                    timeMinutes = saved.timeMinutes
                )
                editorTask = null
            }
        )
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Удалить задачу?") },
            text = { Text("Задача «${task.title}» будет удалена без возможности восстановления.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteTask(task)
                        taskToDelete = null
                    }
                ) { Text("Удалить") }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) { Text("Отмена") }
            }
        )
    }
}

@Composable
private fun DateNavigation(
    selectedDateEpochDay: Long,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onCalendar: () -> Unit
) {
    val today = LocalDate.now().toEpochDay()
    Surface(tonalElevation = 2.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = onPrevious,
                    enabled = selectedDateEpochDay > today
                ) { Text("←") }

                Text(
                    text = formatFullDate(selectedDateEpochDay),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                TextButton(onClick = onNext) { Text("→") }
            }
            TextButton(onClick = onCalendar) { Text("Выбрать в календаре") }
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(tasks, key = Task::id) { task ->
            TaskCard(task = task, onEdit = { onEdit(task) }, onDelete = { onDelete(task) })
        }
    }
}

@Composable
private fun TaskCard(task: Task, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = formatTime(task.timeMinutes),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(76.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (task.description.isNotBlank()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Редактировать") }
                TextButton(onClick = onDelete) { Text("Удалить") }
            }
        }
    }
}

@Composable
private fun EmptyTasks(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "На выбранную дату задач нет",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Нажмите «+», чтобы создать задачу",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    initialDateEpochDay: Long,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit
) {
    val today = LocalDate.now().toEpochDay()
    val state = rememberDatePickerState(
        initialSelectedDateMillis = initialDateEpochDay * MILLIS_PER_DAY,
        selectableDates = remember {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                    utcTimeMillis >= today * MILLIS_PER_DAY
            }
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    state.selectedDateMillis?.let { onDateSelected(it / MILLIS_PER_DAY) }
                },
                enabled = state.selectedDateMillis != null
            ) { Text("Выбрать") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun TaskEditorDialog(
    initialTask: Task,
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit
) {
    var title by remember(initialTask) { mutableStateOf(initialTask.title) }
    var description by remember(initialTask) { mutableStateOf(initialTask.description) }
    var dateEpochDay by remember(initialTask) { mutableLongStateOf(initialTask.dateEpochDay) }
    var timeMinutes by remember(initialTask) { mutableIntStateOf(initialTask.timeMinutes) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initialTask.id == 0L) "Новая задача" else "Редактирование") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = false
                    },
                    label = { Text("Название") },
                    singleLine = true,
                    isError = titleError,
                    supportingText = if (titleError) {
                        { Text("Введите название задачи") }
                    } else {
                        null
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    ) { Text(formatShortDate(dateEpochDay)) }
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.weight(1f)
                    ) { Text(formatTime(timeMinutes)) }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                    } else {
                        onSave(
                            initialTask.copy(
                                title = title,
                                description = description,
                                dateEpochDay = dateEpochDay,
                                timeMinutes = timeMinutes
                            )
                        )
                    }
                }
            ) { Text("Сохранить") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } }
    )

    if (showDatePicker) {
        TaskDatePickerDialog(
            initialDateEpochDay = dateEpochDay,
            onDismiss = { showDatePicker = false },
            onDateSelected = {
                dateEpochDay = it
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        TaskTimePickerDialog(
            initialTimeMinutes = timeMinutes,
            onDismiss = { showTimePicker = false },
            onTimeSelected = {
                timeMinutes = it
                showTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTimePickerDialog(
    initialTimeMinutes: Int,
    onDismiss: () -> Unit,
    onTimeSelected: (Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTimeMinutes / 60,
        initialMinute = initialTimeMinutes % 60,
        is24Hour = true
    )
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Выберите время", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(20.dp))
                TimePicker(state = state)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Отмена") }
                    Button(onClick = { onTimeSelected(state.hour * 60 + state.minute) }) {
                        Text("Выбрать")
                    }
                }
            }
        }
    }
}

private fun formatFullDate(epochDay: Long): String {
    val date = LocalDate.ofEpochDay(epochDay)
    if (date == LocalDate.now()) return "Сегодня, ${formatShortDate(epochDay)}"
    val text = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", RUSSIAN_LOCALE))
    return text.replaceFirstChar { it.titlecase(RUSSIAN_LOCALE) }
}

private fun formatShortDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay)
    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy", RUSSIAN_LOCALE))

private fun formatTime(timeMinutes: Int): String =
    String.format(RUSSIAN_LOCALE, "%02d:%02d", timeMinutes / 60, timeMinutes % 60)

private fun currentTimeMinutes(): Int {
    val now = java.time.LocalTime.now()
    return now.hour * 60 + now.minute
}

private val RUSSIAN_LOCALE = Locale("ru", "RU")
private const val MILLIS_PER_DAY = 86_400_000L
