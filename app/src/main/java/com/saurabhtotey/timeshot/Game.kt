package com.saurabhtotey.timeshot

import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.wearable.activity.WearableActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageView
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
        this.gameState = GameState(GameMode.values().firstOrNull { it.label == intent.getStringExtra("mode") }!!, displaySize.x.toFloat() / 2, displaySize.y.toFloat() / 2)
        this.gestureDetector = GestureDetectorCompat(this, SwipeHandler(this.gameState, this.ball))
        Thread {
            while (!this.gameState.isFinished) {
                this.gameState.update()
                this.updateBallPos()
                Thread.sleep(gameSpeed)
            }
        }.start()
    }

    /**
     * Sets the ball's position to what is specified by in gameState
     */
    private fun updateBallPos() {
        runOnUiThread {
            this.ball.x = this.gameState.currentX - ball.width / 2
            this.ball.y = this.gameState.currentY - ball.height / 2
        }
    }

    /**
     * If a touch is detected, ensures that the gestureDetector can handle the motion
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        val bounds = Rect().also { this.ball.getHitRect(it) }
        val ballBeingFlung = bounds.contains(event!!.x.roundToInt(), event.y.roundToInt())
        this.gestureDetector.onTouchEvent(event)
        return if (ballBeingFlung) {
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
