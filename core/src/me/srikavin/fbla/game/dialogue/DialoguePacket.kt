package me.srikavin.fbla.game.dialogue

/**
 * These are classes that will be sent back-and-forth between a [DialogueCallable] and the [DialogueManager]
 */
sealed class DialoguePacket

/**
 * Represents a request for a user response
 *
 * @param options A list of responses to display and allow the user to choose from
 */
data class RequestResponseDialoguePacket(val options: List<String>) : DialoguePacket()

/**
 * Represents a response to [RequestResponseDialoguePacket]
 *
 * @param index The index of the response the player chose
 */
data class ReceiveResponseDialoguePacket(val index: Int) : DialoguePacket()

/**
 * Represents a request to display some text to the user as if it is coming from the character the user is talking to
 *
 * @param message The message to display
 */
data class SayDialoguePacket(val message: String) : DialoguePacket()

/**
 * Represents a request to get the current score for this dialogue session
 */
object GetScoreDialoguePacket : DialoguePacket()

/**
 * Represents a response to [GetScoreDialoguePacket]
 *
 * @param score The current score for this dialogue session
 */
data class ScoreDialoguePacket(val score: Int) : DialoguePacket()

/**
 * Represents a cue to continue running the dialogue script
 */
object ResumeDialoguePacket : DialoguePacket()

/**
 * Represents a request to update the score based on the relative delta
 *
 * @param delta The change to make to the player's score
 */
data class UpdateScoreDialoguePacket(val delta: Int) : DialoguePacket()

/**
 * Represents a request to end the dialogue session
 */
object EndDialoguePacket : DialoguePacket()
