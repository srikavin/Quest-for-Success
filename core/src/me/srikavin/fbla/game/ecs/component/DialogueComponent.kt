package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import kotlinx.coroutines.channels.Channel
import me.srikavin.fbla.game.dialogue.DialogueCallable
import me.srikavin.fbla.game.dialogue.DialoguePacket

/**
 * Component containing a running dialogue script
 */
class DialogueComponent : Component() {
    lateinit var script: DialogueCallable
    lateinit var channel: Channel<DialoguePacket>
    var finished = false
    var waitingForResponse = false
    var score = 0
}