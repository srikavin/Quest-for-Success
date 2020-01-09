package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueBrochure : DialogueCallable() {

    override fun run() {
        say("Title?")
        var index = getResponse(listOf("FBLA", "Business Achievement Award", "None"))

        if (index == 0) {
            updateScore(0)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }

        say("Format?")
        index = getResponse(listOf("Organized", "Thrown Together"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        say("Topic?")
        index = getResponse(listOf("BAA Award", "How-to invest", "Memes"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(0)
        }
        if (index == 2) {
            updateScore(-1)
        }

        say("Color scheme?")
        index = getResponse(listOf("Green and purple", "Gold and blue", "Black and white"))

        if (index == 0) {
            updateScore(-1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }

        say("Style?")
        index = getResponse(listOf("Bi-fold", "Trifold", "Single sheet"))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }


        val score = getScore()
        if (score > 0) {
            say("You've successfully created a social brochure!")
        }
        if (score <= 0) {
            say("Back to the drawing board")
        }
    }
}
