package me.srikavin.fbla.game.minigame.quiz

/**
 * A model class representing a quiz answer
 */
data class QuizAnswer(
        /**
         * The text contained withing the answer
         */
        var contents: String = "",
        /**
         * Whether or not the answer is correct
         */
        var isCorrect: Boolean = false
)
