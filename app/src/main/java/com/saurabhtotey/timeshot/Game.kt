package com.saurabhtotey.timeshot

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.wearable.activity.WearableActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.TextView
import kotlin.math.roundToInt

/**
 * The actual game that is run
 */
class Game : WearableActivity() {

    private lateinit var ball: ImageView
    private lateinit var gameState: GameState
    private lateinit var gestureDetector: GestureDetectorCompat

    /**
     * When the game is created, initializes the gamestate and starts running the game and updating graphics on a separate thread
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        this.ball = findViewById(R.id.ball)
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
        val bounds = Rect().also { this.ball.getHitRect(it) }
        val ballBeingFlung = bounds.contains(event!!.x.roundToInt(), event.y.roundToInt())
        this.gestureDetector.onTouchEvent(event)
        return if (ballBeingFlung || this.gameState.ballInMotion) {
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
         * When a fling happens in the center, the ball is shot towards that direction
         */
        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, xVelocity: Float, yVelocity: Float): Boolean {
            p0!!
            p1!!
            val bounds = Rect().also { this.ball.getHitRect(it) }
            if (!bounds.contains(p0.x.roundToInt(), p0.y.roundToInt())) {
                return false
            }
            gameState.swipe(xVelocity, yVelocity)
            return true
        }

    }

}
