package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueLetterRec : DialogueCallable() {

    override fun run() {
        say("Hey! Why are you here?")
        var index = getResponse(listOf("Hi! I was wondering if you could write a letter of recommendation for FBLA?.", "You have to write something for me."))

        if (index == 0) {
            updateScore(+1)
        }
        if (index == 1) {
            updateScore(-1)
            say("What is it")
            getResponse(listOf("A letter for FBLA"))
        }

        say("Why do you need it?")
        index = getResponse(listOf("For the Business Achievement Award.", "Because I do."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
            say("Well you're rude.")
            index = getResponse(listOf("Fine. It's for this award."))
        }

        say("Why is it important?")
        index = getResponse(listOf("So I can progress in the field I have a passion for.", "Because it sounds cool."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        say("What do I get out of it?")
        index = getResponse(listOf("The satisfaction of knowing you helped a student reach their goal.", "Nothing."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        say("I might just write it for you out of the kindness of my heart.")
        index = getResponse(listOf("Thank you so much!", "Whatever."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        val score = getScore()
        if (score > 0) {
            say("You got the letter!")
        }
        if (score <= 0) {
            say("She chose not to write the letter.")
        }
    }
}
