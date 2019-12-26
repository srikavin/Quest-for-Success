package me.srikavin.quiz.model

/**
 * A model class representing a quiz as provided by implementations of [me.srikavin.quiz.repository.QuizRepository]
 */
data class Quiz(
        /**
         * A list of questions present withing this quiz
         */
        var questions: MutableList<QuizQuestion> = mutableListOf()
)

