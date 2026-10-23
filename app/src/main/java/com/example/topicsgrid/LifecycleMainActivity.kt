package com.example.topicsgrid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.topicsgrid.ui.theme.TopicsGridTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
        enableEdgeToEdge()
        setContent {
            TopicsGridTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ActivityLifecycleScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        logLifecycle("onPause")
        super.onPause()
    }

    override fun onStop() {
        logLifecycle("onStop")
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
        logLifecycle("onRestart")
    }

    override fun onDestroy() {
        logLifecycle("onDestroy")
        super.onDestroy()
    }

    private fun logLifecycle(methodName: String) {
        Log.d(LOG_TAG, methodName)
    }

    private companion object {
        const val LOG_TAG = "ActivityLifecycle"
    }
}

@Composable
fun ActivityLifecycleScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Activity Lifecycle",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Фильтр Logcat: tag:ActivityLifecycle",
            style = MaterialTheme.typography.titleMedium
        )
        LifecycleHint(
            title = "Запуск",
            sequence = "onCreate → onStart → onResume"
        )
        LifecycleHint(
            title = "Сворачивание и возврат",
            sequence = "onPause → onStop → onRestart → onStart → onResume"
        )
        LifecycleHint(
            title = "Поворот экрана",
            sequence = "Старая Activity уничтожается, затем создаётся заново"
        )
    }
}

@Composable
private fun LifecycleHint(title: String, sequence: String) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = sequence)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ActivityLifecyclePreview() {
    TopicsGridTheme {
        ActivityLifecycleScreen()
    }
}
