package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import kotlinx.coroutines.channels.Channel
import me.srikavin.fbla.game.dialogue.DialogueCallable
import me.srikavin.fbla.game.dialogue.DialoguePacket

class DialogueComponent : Component() {
    lateinit var script: DialogueCallable
    lateinit var channel: Channel<DialoguePacket>
    var waitingForResponse = false
    var score = 0
}