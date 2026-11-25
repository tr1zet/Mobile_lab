package com.example.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.tasks.ui.TasksApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as TasksApplication).appContainer.tasksRepository

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TasksApp(repository = repository)
                }
            }
        }
    }
}
