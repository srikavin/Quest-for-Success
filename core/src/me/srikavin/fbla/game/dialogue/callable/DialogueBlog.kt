package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueBlog : DialogueCallable() {

    override fun run() {
        say("Title?")
        var index = getResponse(listOf("The adventures of FBLA", "Business Achievement Award ultimate guide", "Funny blog"))

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
        index = getResponse(listOf("BAA Award", "Every-day life of FBLA president", "Memes"))

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
        index = getResponse(listOf("Green and purple", "Gold and blue", "Orange and yellow"))

        if (index == 0) {
            updateScore(-1)
        }
        if (index == 1) {
            updateScore(1)
        }
        if (index == 2) {
            updateScore(-1)
        }

        say("Ads?")
        index = getResponse(listOf("Sometimes", "No", "Yes"))

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
            say("You've successfully created a blog!")
        }
        if (score <= 0) {
            say("Time to start over!")
        }
    }
}
