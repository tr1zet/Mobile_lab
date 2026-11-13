package com.example.rickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.rickandmorty.ui.RickAndMortyApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as RickAndMortyApplication)
            .appContainer
            .characterRepository

        setContent {
            MaterialTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    RickAndMortyApp(repository = repository)
                }
            }
        }
    }
}
