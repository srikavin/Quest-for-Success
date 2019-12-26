package me.srikavin.fbla.game.dialogue

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class DialogueCallable {
    private lateinit var channel: Channel<DialoguePacket>

    fun say(str: String): Unit {
        channel.sendBlocking(SayDialoguePacket(str))
        runBlocking {
            when (val packet = channel.receive()) {
                is ResumeDialoguePacket -> return@runBlocking
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ResumeDialoguePacket")
            }
        }
        runBlocking {
            delay(750)
        }
    }

    fun getResponse(responses: List<String>): Int {
        channel.sendBlocking(RequestResponseDialoguePacket(responses))
        return runBlocking {
            when (val packet = channel.receive()) {
                is ReceiveResponseDialoguePacket -> return@runBlocking packet.index
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ReceiveResponseDialoguePacket")
            }
        }
    }

    fun updateScore(delta: Int) {
        channel.sendBlocking(UpdateScoreDialoguePacket(delta))
        runBlocking {
            when (val packet = channel.receive()) {
                is ResumeDialoguePacket -> return@runBlocking
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ResumeDialoguePacket")
            }
        }
    }

    fun getScore(): Int {
        channel.sendBlocking(GetScoreDialoguePacket)
        return runBlocking {
            when (val packet = channel.receive()) {
                is ScoreDialoguePacket -> return@runBlocking packet.score
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ScoreDialoguePacket")
            }
        }
    }

    fun start(channel: Channel<DialoguePacket>) {
        this.channel = channel
        GlobalScope.launch {
            run()
            delay(750)
            val score = getScore()
            say("Score: $score")
            channel.sendBlocking(EndDialoguePacket)
        }
    }

    protected abstract fun run()
}