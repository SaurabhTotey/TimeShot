package com.saurabhtotey.timeshot

import kotlin.math.pow
import kotlin.math.sqrt

/**
 * A set of enums that represent the different gamemodes which affects gameplay and game type
 */
enum class GameMode(val label: String) {
    RANDOM("RANDOM"), CURRENT("CURRENT")
}

const val gameSpeed: Long = 1000 / 20 //The delay between ticks for the game in milliseconds

/**
 * A class that represents how the game is currently
 */
class GameState(val gameMode: GameMode, val initialX: Float, val initialY: Float, val isRound: Boolean, val length: Int) {

    var currentX: Float = initialX
    var currentY: Float = initialY
    val speed = 5
    var velocityX: Float = 0.toFloat()
        private set(value) {
            field = value
        }
    var velocityY: Float = 0.toFloat()
        private set(value) {
            field = value
        }
    var isFinished = false

    /**
     * Performs one game tick and moves the ball
     */
    fun update() {
        if (this.isFinished) {
            return
        }
        this.currentX += this.velocityX
        this.currentY += this.velocityY
        this.isFinished = if (this.isRound) {
            (this.initialX - this.currentX).pow(2) + (this.initialY - this.currentY).pow(2) >= (this.length / 2).toDouble().pow(2)
        } else {
            this.currentX <= 0 || this.currentY <= 0 || this.currentX >= this.length || this.currentY >= this.length
        }
    }

    /**
     * Receives component velocities and sets the ball's velocity as such
     */
    fun swipe(velocityX: Float, velocityY: Float) {
        val magnitude = sqrt(velocityY.pow(2) + velocityX.pow(2))
        this.velocityX = this.speed * velocityX / magnitude
        this.velocityY = this.speed * velocityY / magnitude
    }

}
