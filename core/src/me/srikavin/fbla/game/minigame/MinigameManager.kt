package me.srikavin.fbla.game.minigame

import me.srikavin.fbla.game.minigame.dialogue.DialogueMinigame
import me.srikavin.fbla.game.minigame.dropcatch.DropcatchMinigame

class MinigameManager {
    private val minigames = mapOf(
            "quiz" to DialogueMinigame(),
            "dropcatch" to DropcatchMinigame(),
            "dialogue" to DialogueMinigame()
    )

    fun getMinigame(name: String): Minigame {
        return minigames[name] ?: error("Unknown minigame: $name")
    }
}