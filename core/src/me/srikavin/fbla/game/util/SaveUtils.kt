package me.srikavin.fbla.game.util

import com.badlogic.gdx.Gdx
import ktx.log.error
import me.srikavin.fbla.game.state.GameState

const val SAVE_PATH = "save.dat"

object SaveUtils {
    /**
     * Load the game state from the given file into the given [GameState] instance
     */
    fun loadGame(state: GameState) {
        if (Gdx.files.isLocalStorageAvailable) {
            val file = Gdx.files.local(SAVE_PATH)
//            file.
        } else {
            error { "Local storage is not available on this platform" }
        }
    }

    /**
     * Saves the given game state into the given file
     */
    fun saveGame(state: GameState, name: String) {
        if (Gdx.files.isLocalStorageAvailable) {
            val file = Gdx.files.local(SAVE_PATH)
            file.writeBytes(ByteArray(5), true)
        } else {
            error { "Local storage is not available on this platform" }
        }
    }
}