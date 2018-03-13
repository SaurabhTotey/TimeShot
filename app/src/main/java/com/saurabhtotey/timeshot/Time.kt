package com.saurabhtotey.timeshot

import java.util.*
import kotlin.math.PI

/**
 * A small class to hold time information
 */
class Time(gameMode: GameMode) {
    //The hour component of the time; when set, will never exceed 12
    var hours = if (gameMode == GameMode.RANDOM) (Math.random() * 13).toInt() else if (Calendar.getInstance().time.hours % 12 == 0) 12 else Calendar.getInstance().time.hours % 12
        set(value) {
            field = value % 12
            if (field == 0) {
                field = 12
            }
        }
    //The minutes component of the time; when set, will never exceed 60; will update hours when necessary
    var minutes = if (gameMode == GameMode.RANDOM) (Math.random() * 60).toInt() else Calendar.getInstance().time.minutes
        set(value) {
            field = value
            if (field > 60) {
                field = value % 60
                this.hours++
            }
        }
    //The seconds component of the time; when set, will never exceed 60; will update minutes when necessary
    var seconds = if (gameMode == GameMode.RANDOM) (Math.random() * 60).toInt() else Calendar.getInstance().time.seconds
        set(value) {
            field = value
            if (field > 60) {
                field = value % 60
                this.minutes++
            }
        }

    /**
     * Gets the angle of the hour hand
     */
    fun angle(): Double {
        return -2 * PI * (this.hours / 12.0 + this.minutes / (12.0 * 60.0) + this.seconds / (12.0 * 60.0 * 60.0)) + PI / 2
    }

    /**
     * Returns this time as a pretty string
     */
    override fun toString(): String {
        return "${this.hours}:${this.minutes}:${this.seconds}"
    }
}