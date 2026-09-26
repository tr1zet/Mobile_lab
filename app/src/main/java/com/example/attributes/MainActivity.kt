package com.example.attributes

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Находим EditText
        val editText = findViewById<EditText>(R.id.edit_text)

        findViewById<Button>(R.id.btn_black_text).setOnClickListener {
            editText.setTextColor(Color.BLACK)
        }
        findViewById<Button>(R.id.btn_red_text).setOnClickListener {
            editText.setTextColor(Color.RED)
        }

        findViewById<Button>(R.id.btn_size_8).setOnClickListener {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
        }
        findViewById<Button>(R.id.btn_size_24).setOnClickListener {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
        }

        findViewById<Button>(R.id.btn_bg_white).setOnClickListener {
            editText.setBackgroundColor(Color.WHITE)
        }
        findViewById<Button>(R.id.btn_bg_yellow).setOnClickListener {
            editText.setBackgroundColor(Color.YELLOW)
        }
    }
}
