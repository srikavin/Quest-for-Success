package me.srikavin.fbla.game.dialogue

sealed class DialoguePacket
data class RequestResponseDialoguePacket(val options: List<String>) : DialoguePacket()
data class ReceiveResponseDialoguePacket(val index: Int) : DialoguePacket()
data class SayDialoguePacket(val message: String) : DialoguePacket()
object GetScoreDialoguePacket : DialoguePacket()
data class ScoreDialoguePacket(val score: Int) : DialoguePacket()
object ResumeDialoguePacket : DialoguePacket()
data class UpdateScoreDialoguePacket(val delta: Int) : DialoguePacket()
object EndDialoguePacket : DialoguePacket()
