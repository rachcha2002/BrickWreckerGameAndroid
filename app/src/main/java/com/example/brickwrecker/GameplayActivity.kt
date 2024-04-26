package com.example.brickwrecker


import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler
import android.media.MediaPlayer

class GameplayActivity : AppCompatActivity() {

    private lateinit var scoreText: TextView
    private lateinit var paddle: View
    private lateinit var ball: View
    private lateinit var brickContainer: LinearLayout
    private lateinit var pauseButton: ImageButton
    private lateinit var animator: ValueAnimator

    // Add these class-level properties
    private val handler = Handler()
    private lateinit var runnable: Runnable


    private var ballX = 0f
    private var ballY = 0f
    private var ballSpeedX = 0f
    private var ballSpeedY = 0f
    private var paddleX = 0f
    private var score = 0
    private val brickRows = 9
    private val brickColumns = 10
    private val brickWidth = 100
    private val brickHeight = 40
    private val brickMargin = 4
    private var lives = 3
    private var isGamePaused = false
    private var isAnimating = true // Flag to track animation state
    private val pauseProgress = 0.5f // Pause at 50% progress
    private var paddleHitSound: MediaPlayer? = null
    private var brickHitSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gameplay_activity)

        scoreText = findViewById(R.id.scoreText)
        paddle = findViewById(R.id.paddle)
        ball = findViewById(R.id.ball)
        brickContainer = findViewById(R.id.brickContainer)


        // Initialize pause button
        pauseButton = findViewById(R.id.pauseButton)
        pauseButton.setOnClickListener { showPauseModal() }

        // Initialize MediaPlayer with sound files
        paddleHitSound = MediaPlayer.create(this, R.raw.bounce)
        brickHitSound = MediaPlayer.create(this, R.raw.breaksound)



        val newgame = findViewById<ImageButton>(R.id.newgame)

        newgame.setOnClickListener {
            initializeBricks()
            start()
            newgame.visibility = View.INVISIBLE
        }
    }

    private fun initializeBricks() {
        val brickWidthWithMargin = (brickWidth + brickMargin).toInt()

        for (row in 0 until brickRows) {
            val rowLayout = LinearLayout(this)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.layoutParams = params

            for (col in 0 until brickColumns) {
                val brick = View(this)
                val brickParams = LinearLayout.LayoutParams(brickWidth, brickHeight)
                brickParams.setMargins(brickMargin, brickMargin, brickMargin, brickMargin)
                brick.layoutParams = brickParams
                brick.setBackgroundResource(R.color.brickcolor)
                rowLayout.addView(brick)
            }

            brickContainer.addView(rowLayout)
        }
    }

    private fun moveBall() {
        ballX += ballSpeedX
        ballY += ballSpeedY
        ball.x = ballX
        ball.y = ballY
    }

    private fun movePaddle(x: Float) {
        paddleX = x - paddle.width / 2
        paddle.x = paddleX
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun checkCollision() {
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val screenHeight = resources.displayMetrics.heightPixels.toFloat()

        if (ballX <= 0 || ballX + ball.width >= screenWidth) {
            ballSpeedX *= -1
        }

        if (ballY <= 0) {
            ballSpeedY *= -1
        }

        if (ballY + ball.height >= paddle.y && ballY + ball.height <= paddle.y + paddle.height
            && ballX + ball.width >= paddle.x && ballX <= paddle.x + paddle.width
        ) {
            ballSpeedY *= -1
            score++
            scoreText.text = "Score: $score"
            paddleHitSound?.start()
        }

        if (ballY + ball.height >= screenHeight) {
            resetBallPosition()
        }

        for (row in 0 until brickRows) {
            val rowLayout = brickContainer.getChildAt(row) as LinearLayout

            val rowTop = rowLayout.y + brickContainer.y
            val rowBottom = rowTop + rowLayout.height

            for (col in 0 until brickColumns) {
                val brick = rowLayout.getChildAt(col) as View

                if (brick.visibility == View.VISIBLE) {
                    val brickLeft = brick.x + rowLayout.x
                    val brickRight = brickLeft + brick.width
                    val brickTop = brick.y + rowTop
                    val brickBottom = brickTop + brick.height

                    if (ballX + ball.width >= brickLeft && ballX <= brickRight
                        && ballY + ball.height >= brickTop && ballY <= brickBottom
                    ) {
                        brick.visibility = View.INVISIBLE
                        ballSpeedY *= -1
                        score++
                        scoreText.text = "Score: $score"
                        brickHitSound?.start()
                        return
                    }
                }
            }
        }

        if (ballY + ball.height >= screenHeight - 100) {
            lives--
            if (lives > 0) {
                Toast.makeText(this, "$lives balls left ", Toast.LENGTH_SHORT).show()
            }

            if (lives <= 0) {
                gameOver()
            } else {
                resetBallPosition()
                start()
            }
        }
    }

    private fun resetBallPosition() {
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2 + 525

        ball.x = ballX
        ball.y = ballY

        ballSpeedX = 0 * screenDensity
        ballSpeedY = 0 * screenDensity

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX
    }

    private fun gameOver() {
        val intent = Intent(this, GameOverActivity::class.java)
        intent.putExtra("SCORE", score)
        startActivity(intent)
        finish()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun movePaddle() {
        paddle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    movePaddle(event.rawX)
                }
            }
            true
        }
    }

    private fun start() {
        movePaddle()
        val displayMetrics = resources.displayMetrics
        val screenDensity = displayMetrics.density

        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        paddleX = screenWidth / 2 - paddle.width / 2
        paddle.x = paddleX

        ballX = screenWidth / 2 - ball.width / 2
        ballY = screenHeight / 2 - ball.height / 2

        val brickHeightWithMargin = (brickHeight + brickMargin * screenDensity).toInt()

        ballSpeedX = 3 * screenDensity
        ballSpeedY = -3 * screenDensity

        animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = Long.MAX_VALUE
        animator.interpolator = LinearInterpolator()
        animator.addUpdateListener { animation ->
            moveBall()
            checkCollision()
        }
        animator.start()
    }

    // Function to pause the game (stop animations)
    private fun pauseGame() {
        // Pause any ongoing animations (e.g., ball movement, paddle animation)
        // Implement your specific logic here
        isGamePaused = true
    }

    // Function to resume the game (resume animations)
    private fun resumeGame() {
        // Resume animations (e.g., ball movement, paddle animation)
        // Implement your specific logic here
        isGamePaused = false
    }

    // Corrected onPause function
    override fun onPause() {
        super.onPause()
        // Pause the game loop when the activity is paused
        if (::animator.isInitialized && animator.isRunning) {
            animator.pause()
        }
    }

    // Corrected onResume function
    override fun onResume() {
        super.onResume()
        // Resume the game loop when the activity is resumed

        if (::animator.isInitialized ) {
            animator.resume()
        }
    }


    // Function to show the pause modal
    private fun showPauseModal() {
        onPause() // Pause the game when the modal is displayed
        //pauseGame()

        val modalView = layoutInflater.inflate(R.layout.modal_pause, null) // Replace with your modal layout

        val resumeButton = modalView.findViewById<ImageButton>(R.id.resumeButton)
        val exitButton = modalView.findViewById<ImageButton>(R.id.exitButton)

        val alertDialog = AlertDialog.Builder(this)
            .setView(modalView)
            .create()

        // Handle button clicks:
        resumeButton.setOnClickListener {
            // Resume the game when the user clicks on the resume button
            onResume()
            //resumeGame()
            alertDialog.dismiss()
        }

        exitButton.setOnClickListener {
            // Implement logic to handle exiting the game
            if (score > 0) {
                gameOver()
            } else {
                // Handle other cases (if needed)
            }
            finish() // Navigate back to the previous activity
        }

        // Show the modal after a short delay
        Handler().postDelayed({
            alertDialog.show()
        }, 100) // Adjust the delay time as needed (e.g., 100 milliseconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release MediaPlayer resources
        paddleHitSound?.release()
        brickHitSound?.release()
    }

}
