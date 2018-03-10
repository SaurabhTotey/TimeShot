package com.saurabhtotey.timeshot

import java.util.*
import kotlin.math.*

/**
 * A set of enums that represent the different gamemodes which affects gameplay and game type
 */
enum class GameMode(val label: String) {
    RANDOM("RANDOM"), CURRENT("CURRENT")
}

/**
 * A small class to hold time information
 */
class Time(gameMode: GameMode) {
    var hours = if (gameMode == GameMode.RANDOM) (Math.random() * 25).toInt() else Calendar.getInstance().time.hours
        set(value) {
            field = value % 24
        }
    var minutes = if (gameMode == GameMode.RANDOM) (Math.random() * 60).toInt() else Calendar.getInstance().time.minutes
        set(value) {
            field = value
            if (field > 60) {
                field = value % 60
                this.hours++
            }
        }
    var seconds = if (gameMode == GameMode.RANDOM) (Math.random() * 60).toInt() else Calendar.getInstance().time.seconds
        set(value) {
            field = value
            if (field > 60) {
                field = value % 60
                this.minutes++
            }
        }
    fun angle(): Double {
        return -2 * PI * (this.hours / 12 + this.minutes / (12 * 60) + this.seconds / (12 * 60 * 60)) + PI / 2
    }
    override fun toString(): String {
        return "${this.hours}:${this.minutes}:${this.seconds}"
    }
}

const val gameSpeed: Long = 1000 / 20 //The delay between ticks for the game in milliseconds

/**
 * A class that represents how the game is currently
 */
class GameState(gameMode: GameMode, val initialX: Float, val initialY: Float, val isRound: Boolean, val length: Int) {

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
    val timeToShootFor = Time(gameMode)

    /**
     * When the gamestate is made, it increments the time
     */
    init {
//        while (!this.isFinished) {
//            Thread.sleep(1000)
//            this.timeToShootFor.seconds += 1
//        }
    }

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
        val timeAngle = processAngle(this.timeToShootFor.angle())
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
