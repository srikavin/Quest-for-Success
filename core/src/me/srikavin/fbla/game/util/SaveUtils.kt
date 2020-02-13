package me.srikavin.fbla.game.util

import com.badlogic.gdx.Gdx
import ktx.log.error
import me.srikavin.fbla.game.state.GameState

const val SAVE_PATH = "saves/save.dat"

object SaveUtils {
    /**
     * Load the game state from the given file into the given [GameState] instance
     */
    fun loadGame(state: GameState) {
        try {
            if (Gdx.files.isLocalStorageAvailable) {
                val file = Gdx.files.local(SAVE_PATH)
                val reader = file.reader()

                state.lives = reader.read()
                state.score = reader.read()

                val levelPathBuf = CharArray(reader.read())
                reader.read(levelPathBuf)

                state.currentLevelPath = String(levelPathBuf)

                state.awards.clear()

                for (i in 0 until reader.read()) {
                    val nameBuf = CharArray(reader.read())
                    reader.read(nameBuf)

                    state.addAward(String(nameBuf))
                }
                state.lastAddedAward = null
                reader.close()

            } else {
                error { "Local storage is not available on this platform" }
            }
        } catch (e: Exception) {
            error(e)
        }
    }

    /**
     * Saves the given game state into the given file
     */
    fun saveGame(state: GameState) {
        try {
            if (Gdx.files.isLocalStorageAvailable) {
                val file = Gdx.files.local(SAVE_PATH)
                val writer = file.writer(false)
                writer.write(state.lives)
                writer.write(state.score)
                writer.write(state.currentLevelPath.length)
                writer.write(state.currentLevelPath.toCharArray())
                writer.write(state.awards.size)
                state.awards.forEach {
                    writer.write(it.getName().length)
                    writer.write(it.getName().toCharArray())
                }
                writer.close()
            } else {
                error { "Local storage is not available on this platform" }
            }
        } catch (e: Exception) {
            error(e)
        }

    }

    fun saveGameExists(): Boolean {
        if (Gdx.files.isLocalStorageAvailable) {
            val file = Gdx.files.local(SAVE_PATH)

            return file.exists()
        }

        return false
    }
}