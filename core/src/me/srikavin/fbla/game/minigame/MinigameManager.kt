package me.srikavin.fbla.game.minigame

import me.srikavin.fbla.game.minigame.dropcatch.DropcatchMinigame
import me.srikavin.fbla.game.minigame.quiz.QuizMinigame

class MinigameManager {
    private val minigames = mapOf(
            "quiz" to QuizMinigame(),
            "dropcatch" to DropcatchMinigame(),
            "dialogue" to QuizMinigame(),
            "buttonmash" to QuizMinigame()
    )

    fun getMinigame(name: String): Minigame {
        return minigames[name] ?: error("Unknown minigame: $name")
    }
}