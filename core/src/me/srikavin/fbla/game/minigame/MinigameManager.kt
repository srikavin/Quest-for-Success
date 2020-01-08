package me.srikavin.fbla.game.minigame

import me.srikavin.fbla.game.minigame.quiz.QuizMinigame

class MinigameManager {
    val minigames = mapOf<String, Minigame>(
            "quiz" to QuizMinigame()
//          "dialogue"
//          "buttonmash"
//          "dropcatch"
    )

    fun getMinigame(name: String): Minigame {
        return minigames[name] ?: error("Unknown minigame: $name")
    }
}