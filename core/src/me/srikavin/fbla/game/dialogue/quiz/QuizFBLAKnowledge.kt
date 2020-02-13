package me.srikavin.fbla.game.dialogue.quiz

import me.srikavin.fbla.game.dialogue.DialogueCallable

class QuizFBLAKnowledge : DialogueCallable() {

    override fun run() {
        say("When was the name \"Future Business Leaders of America\" chosen for the organization?")
        var index = getResponse(listOf("1945", "1940", "1920", "1937"))

        if (index == 1) {
            updateScore(1)
            say("Good job!")
        } else {
            say("The right answer was 1940.")
        }

        say("What state had the first FBLA state chapter?")
        index = getResponse(listOf("Iowa", "Delaware", "Wisconsin", "Tennessee"))

        if (index == 0) {
            updateScore(1)
            say("That's right!")
        } else {
            say("The right answer was Iowa.")
        }


        say("When was Edward D. Miller appointed as full-time executive director?")
        index = getResponse(listOf("1956", "1970", "1973", "2019"))

        if (index == 2) {
            updateScore(1)
            say("That's perfect!")
        } else {
            say("The right answer was 1973.")
        }


        say("What year did FBLA membership exceed 200,000?")
        index = getResponse(listOf("1982", "1987", "1977", "1964"))

        if (index == 1) {
            say("That's correct!")
            updateScore(1)
        } else {
            say("The right answer was 1987.")
            updateScore(1)
        }


        say("What year was the FBLA Middle Level Division created?")
        index = getResponse(listOf("1997", "1936", "2004", "1994"))

        if (index == 3) {
            updateScore(1)
            say("Good job!")
        }


        val score = getScore()
        if (score > 3) {
            say("You really aced this FBLA history quiz!")
        }
    }
}
