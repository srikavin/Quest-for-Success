package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueMakeChapter : DialogueCallable() {

    override fun run() {
        say("Hey there!")
        var index = getResponse(listOf("Hi! I was wondering if you would like to join an organization called FBLA?", "Go away."))

        if (index == 0) {
            updateScore(+1)
        }
        if (index == 1) {
            updateScore(-1)
            say("Aren't you in an organization called FBLA?")
        }


        say("What is that?")
        index = getResponse(listOf("It is an organization that allows those who hope to go into a business field gain experience and knowledge through a close community.", "I don't know I'm just in it."))

        if (index == 0) {
            updateScore(1)
            say("Wow! I'm a business major!")
        }
        if (index == 1) {
            updateScore(-1)
            say("Well you don't sound very committed.")
        }

        say("How might I join?")
        index = getResponse(listOf("Well you could apply to start a chapter at your university.", "Google it."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
            say("It says here I need to apply to start a chapter.")
        }

        say("How do I do that?")
        index = getResponse(listOf("Maybe we could talk with our advisor at my school.", "Figure it out bro, my work here is done."))

        if (index == 0) {
            updateScore(1)
            say("We should!")
        }
        if (index == 1) {
            updateScore(-1)
            say("Don't you have an advisor or someone I can talk to?")
            getResponse(listOf("Oh yeah."))
        }

        say("This sounds pretty interesting, I think I might look into it.")
        index = getResponse(listOf("You really should!", "Not really, it's boring."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        val score = getScore()
        if (score > 0) {
            say("Thank you for talking to me about this, I believe I might just join!")
        }
        if (score <= 0) {
            say("I don't think this is my kind of thing, goodbye.")
        }
    }
}
