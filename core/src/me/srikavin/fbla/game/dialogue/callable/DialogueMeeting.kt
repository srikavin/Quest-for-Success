package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueMeeting : DialogueCallable() {

    override fun run() {
        say("Why are you here?")
        var index = getResponse(listOf("I'm here to ask if you want to go to a meeting for FBLA", "I forgot"))

        if (index == 0) {
            updateScore(+1)
        }
        if (index == 1) {
            updateScore(-1)
            say("Forgot what?")
        }


        say("The FBLA meeting is tomorrow!")
        index = getResponse(listOf("Would you like to go with me?", "Nevermind, you are not in FBLA"))

        if (index == 0) {
            updateScore(1)
            say("Sure I would love to")
        }
        if (index == 1) {
            updateScore(-1)
            say("I might want to go")
        }


        say("Why should I go?")
        index = getResponse(listOf("FBLA is an extremely beneficial organization, it allows you to grow as a person as well as continue to pursue a career path in business.", "I have no clue, I'm just doing it for this stupid award thing."))

        if (index == 0) {
            updateScore(1)
            say("Wow, that sounds pretty interesting.")
        }
        if (index == 1) {
            updateScore(-1)
            say("Well I thought FBLA was a pretty interesting organization.")
        }


        say("Well what time is the meeting?")
        index = getResponse(listOf("Right after school", "Sometime soon probably."))

        if (index == 0) {
            updateScore(1)
            say("My schedule is free around that time")
        }
        if (index == 1) {
            updateScore(-1)
            say("If it is after school, then I can go")
            getResponse(listOf("Oh yeah it is after school"))
        }

        say("What is the meeting about?")
        index = getResponse(listOf("Today we are meeting about the districts award ceremony that is coming up soon.", "I have no clue"))

        if (index == 0) {
            updateScore(1)
            say("Well that sounds interesting")
        }
        if (index == 1) {
            updateScore(-1)
            say("Well i hope it will be fun at least.")
        }


        val score = getScore()
        if (score > 0) {
            say("Well, I think I might just tag along.")
        } else if (score <= 0) {
            say("I might have to leave after school, I don't want to go")
        }
    }
}