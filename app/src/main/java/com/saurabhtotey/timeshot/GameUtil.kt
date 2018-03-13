package com.saurabhtotey.timeshot

/**
 * A set of enums that represent the different gamemodes which affects gameplay and game type
 */
enum class GameMode(val label: String) {
    RANDOM("RANDOM"), CURRENT("CURRENT")
}

const val gameSpeed: Long = 1000 / 20 //The delay between ticks for the game in milliseconds
