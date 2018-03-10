package com.saurabhtotey.timeshot

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View

/**
 * The activity where the user is first brought to
 * Allows them to choose which activity they want to go to next
 */
class MainMenu : WearableActivity() {

    /**
     * Nothing special is done when the activity is created
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
    }

    private fun playGame(mode: GameMode) {
        val nextActivity = Intent(this, Game::class.java)
        nextActivity.putExtra("mode", mode.label)
        startActivity(nextActivity)
    }

    /**
     * A function that gets called when the user presses the play game button
     * Starts the game activity
     */
    fun playRandom(v: View) {
        playGame(GameMode.RANDOM)
    }

    /**
     * TODO: documentation
     */
    fun playCurrent(v: View) {
        playGame(GameMode.CURRENT)
    }

}
