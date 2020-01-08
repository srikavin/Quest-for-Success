package me.srikavin.fbla.game.dialogue.callable

import me.srikavin.fbla.game.dialogue.DialogueCallable

class DialogueSpeech : DialogueCallable() {

    override fun run() {

        var index = getResponse(listOf("This year I plan to grow our membership and secure more internships for everyone.", "I just need this position for this award thing."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        index = getResponse(listOf("Hopefully, my passion for this organization will pass onto the newer members as well.", "Although I don't really like the club, it's good for colleges."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }
        index = getResponse(listOf("So for more opportunities and a better experience in the club, vote for me!", "So like, make sure I win."))

        if (index == 0) {
            updateScore(1)
        }
        if (index == 1) {
            updateScore(-1)
        }

        val score = getScore()
        if (score > 0) {
            say("You've won the presidency!")
        }
        if (score <= 0) {
            say("Someone beat you out for office.")
        }
    }
}
