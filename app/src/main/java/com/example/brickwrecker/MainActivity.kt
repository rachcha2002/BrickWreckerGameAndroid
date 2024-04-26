package com.example.brickwrecker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve BestScore value from SharedPreferences, defaulting to 0 if not found
        val bestScore = sharedPreferences.getInt("BestScore", 0)


        val scoreText = findViewById<TextView>(R.id.bestScoreText)
        scoreText.text = "Best Score: $bestScore"

        val playButton = findViewById<ImageButton>(R.id.playButton)

        playButton.setOnClickListener {
            val intent = Intent(this, GameplayActivity::class.java)
            startActivity(intent)
        }
    }
}
