package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueJobInterview : DialogueCallable() {

    override fun run() {
        say("Job Title?")
        var index = getResponse(listOf("Accountant", "Engineer", "Artist"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(1)
        }


        say("Education?")
        index = getResponse(listOf("High school", "College"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }


        say("Experience?")
        index = getResponse(listOf("None", "Some in the field", "Some in general"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(1)
        }


        say("Highest position held?")
        index = getResponse(listOf("Worker", "Manager", "CEO"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(1)
        }


        say("Service hours completed")
        index = getResponse(listOf("50", "100", "150"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(1)
        }


        val score = getScore()
        if (score > 0) {
            say("Congratulations, you completed the application!")
        }
        if (score <= 0) {
            say("How?")
        }
    }
}
