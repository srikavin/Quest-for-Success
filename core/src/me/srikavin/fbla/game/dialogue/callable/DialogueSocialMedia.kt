package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueSocialMedia : DialogueCallable() {

    override fun run() {
        say("Title?")
        var index = getResponse(listOf("Club", "FBLA", "None"))

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
        index = getResponse(listOf("BAA Award", "Club in general", "Cute dogs"))

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
        index = getResponse(listOf("Red, blue, white", "Gold and blue", "Black and white"))

        if (index == 0) {
            updateScore(0)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }

        say("Platform?")
        index = getResponse(listOf("Instagram", "Facebook", "MySpace"))

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
            say("You've successfully created a social media post!")
        }
        if (score <= 0) {
            say("Back to the drawing board")
        }
    }
}
