package me.srikavin.fbla.game.minigame.quiz

import java.util.*

/**
 * Represents a game as provided by implementations of [GameRepository]
 */
class Game(
        /**
         * The quiz this game is playing
         */
        val quiz: Quiz
) {
    /**
     * The total number of questions contained in the quiz
     */
    val total: Int = quiz.questions.size
    /**
     * A list of answers chosen by the game player
     */
    val chosen: MutableList<QuizAnswer?> = ArrayList()
    /**
     * The current score held by the game player
     */
    var score: Int = 0
    /**
     * The current number of correct answers received from the game player
     */
    var correct = 0

    internal var waitingForAnswer = false

    /**
     * The current game state
     */
    var state: QuizGameState = QuizGameState.WAITING

    /**
     * The index of the current question
     */
    var currentQuestion = -1
        set(value) {
            field = value
            state.currentQuestion = value
        }

    init {
        quiz.questions.shuffle()
    }
}