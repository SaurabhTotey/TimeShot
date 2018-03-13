package com.saurabhtotey.timeshot

import android.graphics.Point
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.wearable.activity.WearableActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.pow

/**
 * The actual game that is run
 */
class Game : WearableActivity() {

    private lateinit var ball: ImageView
    private lateinit var gameState: GameState
    private lateinit var gestureDetector: GestureDetectorCompat

    /**
     * When this activity is created, it sets the activity's fields which are data it needs to access and then starts running the game
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        this.ball = findViewById(R.id.ball)
        this.createRunningGame()
    }

    /**
     * Creates a game and starts running it on a separate thread
     */
    fun createRunningGame(v: View? = null) {
        val displaySize = Point().also { windowManager.defaultDisplay.getSize(it) }
        this.gameState = GameState(GameMode.values().firstOrNull { it.label == intent.getStringExtra("mode") }!!, displaySize.x.toFloat() / 2, displaySize.y.toFloat() / 2, true, displaySize.x)
        this.gestureDetector = GestureDetectorCompat(this, SwipeHandler(this.gameState, this.ball))
        Thread {
            while (!this.gameState.isFinished) {
                this.gameState.update()
                this.matchScreenToGameState()
                Thread.sleep(gameSpeed)
            }
            this.matchScreenToGameState()
        }.start()
    }

    /**
     * Updates the graphics on the watch screen to accurately represent the game state
     * Manages showing the ball in the correct location and showing the game's time
     * Also displays the score if the game is finished
     */
    private fun matchScreenToGameState() {
        runOnUiThread {
            findViewById<TextView>(R.id.timeBox).setText(this.gameState.timeToShootFor.toString()) //Using property access syntax (.text = ) errors
            if (this.gameState.isFinished) {
                findViewById<TextView>(R.id.scoreBox).setText(this.gameState.score()!!.toString())
                findViewById<ImageButton>(R.id.restartButton).visibility = View.VISIBLE
            } else {
                findViewById<TextView>(R.id.scoreBox).setText("")
                findViewById<ImageButton>(R.id.restartButton).visibility = View.INVISIBLE
            }
            this.ball.x = this.gameState.currentX - ball.width / 2
            this.ball.y = this.gameState.currentY - ball.height / 2
        }
    }

    /**
     * If a touch is detected, ensures that the gestureDetector can handle the motion
     * If the ball is touched or is in motion, the touch event is consumed, otherwise, the watch handles the event
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event!!
        this.gestureDetector.onTouchEvent(event)
        return if (SwipeHandler.touchTouchesBall(event, this.ball) || this.gameState.ballInMotion) {
            true
        } else {
            super.dispatchTouchEvent(event)
        }
    }

    /**
     * An internal class that handles taking in touch events
     */
    class SwipeHandler(var gameState: GameState, val ball: ImageView) : GestureDetector.SimpleOnGestureListener() {

        /**
         * Defines a static function that determines whether a touch is touching the ball
         * Gives the ball a larger area to be touched than just its current rectangle of location
         */
        companion object {
            fun touchTouchesBall(event: MotionEvent, ball: ImageView): Boolean {
                return (event.x - ball.x - ball.width / 2).pow(2) + (event.y - ball.y - ball.height / 2).pow(2) <= 2 * ball.width.toDouble().pow(2)
            }
        }

        /**
         * When a fling happens in the center, the ball is shot towards that direction
         */
        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, xVelocity: Float, yVelocity: Float): Boolean {
            p0!!
            p1!!
            if (!touchTouchesBall(p0, this.ball)) {
                return false
            }
            gameState.swipe(xVelocity, yVelocity)
            return true
        }

    }

}
