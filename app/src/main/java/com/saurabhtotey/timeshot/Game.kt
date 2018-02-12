package com.saurabhtotey.timeshot

import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent

/**
 * The actual game that is run
 */
class Game : WearableActivity() {

    private lateinit var gameState: GameState
    private lateinit var gestureDetector: GestureDetectorCompat

    /**
     * When the game is created, TODO: do stuff
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        this.gameState = GameState(GameMode.values().firstOrNull { it.label == intent.getStringExtra("mode") }!!)
        this.gestureDetector = GestureDetectorCompat(this, SwipeHandler(this.gameState))
    }

    /**
     * If a touch is detected, ensures that the gestureDetector can handle the motion
     */
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return this.gestureDetector.onTouchEvent(event)
    }

    /**
     * An internal class that handles taking in touch events
     */
    class SwipeHandler(val gameState: GameState): GestureDetector.SimpleOnGestureListener() {

        /**
         * When a fling happens in the center, the ball is shot towards that direction
         */
        override fun onFling(p0: MotionEvent?, p1: MotionEvent?, xVelocity: Float, yVelocity: Float): Boolean {
            Log.d("TRACE", "User has made a fling gesture from (${p0!!.x}, ${p0.y}) to (${p1!!.x}, ${p1.y})")
            return true
        }

    }

}
