package com.saurabhtotey.timeshot

/**
 * A set of enums that represent the different gamemodes which affects gameplay and game type
 */
enum class GameMode(val label: String) {
    RANDOM("RANDOM"), CURRENT("CURRENT")
}

/**
 * A class that represents how the game is currently
 */
class GameState(val gameMode: GameMode) {

    var isFinished = false

}
