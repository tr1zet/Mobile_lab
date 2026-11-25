package com.example.tasks

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.tasks.data.OfflineTasksRepository
import com.example.tasks.data.TasksRepository
import com.example.tasks.data.local.TasksDatabase

private val Context.settingsDataStore by preferencesDataStore(name = "task_settings")

class TasksApplication : Application() {
    val appContainer: AppContainer by lazy { DefaultAppContainer(this) }
}

interface AppContainer {
    val tasksRepository: TasksRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: TasksDatabase = Room.databaseBuilder(
        context,
        TasksDatabase::class.java,
        "tasks_database"
    ).build()

    override val tasksRepository: TasksRepository = OfflineTasksRepository(
        taskDao = database.taskDao(),
        dataStore = context.settingsDataStore
    )
}
