package com.example.brickwrecker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.ImageView


class GameOverActivity : Activity() {
        private lateinit var sharedPreferences: SharedPreferences
        private var bestScore = 0


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

                // Display congratulations message or image
                val congratsImage = findViewById<ImageView>(R.id.congratsImage)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Retrieve BestScore value from SharedPreferences, defaulting to 0 if not found
        bestScore = sharedPreferences.getInt("BestScore", 0)

        val score = intent.getIntExtra("SCORE", 0)
        val scoreText = findViewById<TextView>(R.id.scoreText)
        scoreText.text = "Score: $score"

                if(bestScore<score){
                        congratsImage.visibility = View.VISIBLE // Change visibility to "visible"
                }

                val bestScoreText = findViewById<TextView>(R.id.bestestScoreText)
                bestScoreText.text = "Best Score: $bestScore"

                // Update the best score if the current score is higher
                if (score > bestScore) {



                        bestScore = score
                        val editor = sharedPreferences.edit()
                        editor.putInt("BestScore", bestScore)
                        editor.apply()


                }
        val replayButton = findViewById<ImageButton>(R.id.replayButton)
        replayButton.setOnClickListener {
        val intent = Intent(this, GameplayActivity::class.java)
        startActivity(intent)
        finish()
        }

        val menuButton = findViewById<ImageButton>(R.id.menuButton)
        menuButton.setOnClickListener {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        }
        }
        }
