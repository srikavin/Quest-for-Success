package me.srikavin.fbla.game.dialogue

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ktx.log.info

/**
 * The base class that all dialogues inherit from. This class manages communication with a [DialogueManager] through
 * Kotlin Rendezvous [Channel]s. All inheritable functions are blocking and will not return until a response packet
 * is received from the DialogueManager.
 */
abstract class DialogueCallable {
    private lateinit var channel: Channel<DialoguePacket>

    /**
     * Sends a request to display a string to the user as if a game character is saying it. An artificial delay is added
     * to provide time between multiple calls to this function.
     *
     * This function returns after the entire text has been displayed to the user and an additional delay has passed.
     *
     * @param str The message to display to the user
     *
     * @see SayDialoguePacket
     * @see ResumeDialoguePacket
     */
    fun say(str: String) {
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

    /**
     * This function delays future calls to allow for speech-style dialogues.
     */
    fun delay() {
        runBlocking {
            delay(750)
        }
    }

    /**
     * Sends a request to receive a response from the user based on the given list of choices. This call blocks until
     * the user has chosen a response.
     *
     * @param responses The list of possible responses to provide the user
     * @return The index of the response that the user chose in [responses]
     *
     * @see RequestResponseDialoguePacket
     * @see ReceiveResponseDialoguePacket
     */
    fun getResponse(responses: List<String>): Int {
        channel.sendBlocking(RequestResponseDialoguePacket(responses))

        return runBlocking {
            when (val packet = channel.receive()) {
                is ReceiveResponseDialoguePacket -> return@runBlocking packet.index
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ReceiveResponseDialoguePacket")
            }
        }
    }

    /**
     * Sends a request to update the score for this dialogue instance. This call blocks until the request has been
     * acknowledged by the DialogueManager.
     *
     * @param delta The relative score to add (or subtract) from the player's current score
     *
     * @see UpdateScoreDialoguePacket
     * @see ResumeDialoguePacket
     */
    fun updateScore(delta: Int) {
        channel.sendBlocking(UpdateScoreDialoguePacket(delta))

        runBlocking {
            when (val packet = channel.receive()) {
                is ResumeDialoguePacket -> return@runBlocking
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ResumeDialoguePacket")
            }
        }
    }

    /**
     * Sends a request to receive the current score for this dialogue instance. This call blocks until the request has been
     * serviced by the DialogueManager.
     *
     * @return The player's current score
     *
     * @see GetScoreDialoguePacket
     * @see ScoreDialoguePacket
     */
    fun getScore(): Int {
        channel.sendBlocking(GetScoreDialoguePacket)

        return runBlocking {
            when (val packet = channel.receive()) {
                is ScoreDialoguePacket -> return@runBlocking packet.score
                else -> throw RuntimeException("Received ${packet.javaClass.name} instead of ScoreDialoguePacket")
            }
        }
    }

    /**
     * Starts running the dialogue script and uses the given callable to communicate with a DialogueManager (or another
     * class that follows the requirements of a DialogueManager). This call will not block and will be run on a
     * coroutine thread.
     *
     * @param channel The channel to use to communicate requests and responses with a DialogueManager
     */
    fun start(channel: Channel<DialoguePacket>) {
        this.channel = channel
        try {
            GlobalScope.launch {
                // Run the script
                run()

                delay(750)

                // Display the score gained (or lost) by the user
                val score = getScore()
                say("Score: $score")

                // Tell the DialogueManager that we're done
                channel.sendBlocking(EndDialoguePacket)
            }
        } catch (e: ClosedReceiveChannelException) {
            info(e) { "Channel closed" }
        }
    }

    /**
     * Runs the dialogue script. This function should only interact with the superclass [DialogueCallable] without
     * reading or modifying any external data to avoid concurrency issues.
     */
    protected abstract fun run()
}