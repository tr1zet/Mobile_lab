package com.example.tasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.tasks.series.SeriesApplication
import com.example.tasks.series.ui.SeriesApp
import com.example.tasks.series.ui.SeriesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as SeriesApplication).repository

        setContent {
            SeriesTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SeriesApp(repository = repository)
                }
            }
        }
    }
}
