package com.saurabhtotey.timeshot

import kotlin.math.*

/**
 * A class that represents how the game is currently
 * Is created with the type of game, the starting coordinates, whether the screen is round or not, and the length of the screen (radius or square side length)
 */
class GameState(gameMode: GameMode, val initialX: Float, val initialY: Float, val isRound: Boolean, val length: Int) {

    var currentX: Float = initialX //The ball's current X position
    var currentY: Float = initialY //The ball's current Y position
    val speed = 5 //The ball's max speed; TODO: scale off of length
    var velocityX: Float = 0.toFloat() //The ball's X velocity; can't be changed outside of this class
        private set(value) {
            field = value
        }
    var velocityY: Float = 0.toFloat() //The ball's Y velocity; can't be changed outside of this class
        private set(value) {
            field = value
        }
    var isFinished = false
    val timeToShootFor = Time(gameMode)
    var ballInMotion = false
        get() {
            return field && !this.isFinished
        }

    /**
     * When the game is created, it starts a new thread that continually updates the game's time by 1 second every second
     * TODO: app doesn't work when this included; fix
     */
    init {
//        while (!this.isFinished) {
//            Thread.sleep(1000)
//            this.timeToShootFor.seconds += 1
//        }
    }

    /**
     * Performs one game tick and moves the ball
     * Also calculates if the game is finished or not
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
     * If called before game finish, returns null
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
        this.ballInMotion = true
        val magnitude = sqrt(velocityY.pow(2) + velocityX.pow(2))
        this.velocityX = this.speed * velocityX / magnitude
        this.velocityY = this.speed * velocityY / magnitude
    }

}
