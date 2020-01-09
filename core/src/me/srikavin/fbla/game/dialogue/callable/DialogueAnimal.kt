package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueAnimal : DialogueCallable() {

    override fun run() {
        say("Do you like animals?")
        var index = getResponse(listOf("Kind of", "They're lovely", "I hate them"))

        if (index == 0) {
            updateScore(0)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }
        say("How much experience do you have with them?")
        index = getResponse(listOf("Enough", "None"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        say("Why do you want to work with them?")
        index = getResponse(listOf("It's a passion of mine", "To further my progress in my award.", "No clue, I was told to show up here"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(0)
        }
        if (index == 2) {
            updateScore(-1)
        }

        say("Which animals would you like to work with?")
        index = getResponse(listOf("Cats", "Dogs", "Horses"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(1)
        }

        say("Hope you have fun!")
        index = getResponse(listOf("Sure", "I will!", "I wish I would."))

        if (index == 0) {
            updateScore(0)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }


        val score = getScore()
        if (score > 0) {
            say("You've completed your volunteering!")
        }
        if (score <= 0) {
            say("They kicked you out.")
        }
    }
}
