package com.saurabhtotey.timeshot

import java.sql.Time
import java.util.*
import kotlin.math.*

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
    val timeToShootFor = Time((Math.random() * 13).toInt(), (Math.random() * 60).toInt(), (Math.random() * 60).toInt())

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
     * Gets the score for the game; should only be called after game is finished
     * TODO: doesn't work
     */
    fun score(): Int? {
        if (!this.isFinished) {
            return null
        }
        fun angleOfTime(time: Time): Double {
            return -2 * PI * (time.hours / 12 + time.minutes / (12 * 60) + time.seconds / (12 * 60 * 60)) + PI / 2
        }
        fun processAngle(angle: Double): Double {
            var a = angle
            while (a < 0) {
                a += 2 * PI
            }
            while (a > 2 * PI) {
                a -= 2 * PI
            }
            return a
        }
        val ballAngle = processAngle(atan2(this.initialY - this.currentY, this.currentX - this.initialX).toDouble()) //Y is inverted because coordinates go top down
        val timeAngle = processAngle(angleOfTime(if (this.gameMode == GameMode.CURRENT) Time(Calendar.getInstance().time.time) else this.timeToShootFor))
        return (abs(ballAngle - timeAngle) / (2 * PI) * 1000).roundToInt()
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
