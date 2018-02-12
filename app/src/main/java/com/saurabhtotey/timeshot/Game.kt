package com.saurabhtotey.timeshot

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

enum class GameMode(val label: String) {
    RANDOM("RANDOM"), CURRENT("CURRENT")
}

/**
 * The actual game that is run
 */
class Game : WearableActivity() {

    lateinit var gameMode: GameMode //The gamemode of the activity determines how it will behave and what the gameplay will be like

    /**
     * When the game is created, TODO: do stuff
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        this.gameMode = GameMode.values().firstOrNull { it.label == intent.getStringExtra("mode") }!!
    }
}
