package me.srikavin.fbla.game.dialogue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.rafaskoberg.gdx.typinglabel.TypingAdapter
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import kotlinx.coroutines.channels.sendBlocking
import ktx.actors.onClickEvent
import me.srikavin.fbla.game.dialogue.callable.*
import me.srikavin.fbla.game.ecs.component.DialogueComponent

class DialogueManager(private val stage: Stage, skin: Skin) {
    private val dialogueRoot: Table
    private val dialogueTextContainer: Container<TypingLabel>
    private val dialogueOptionsTable: Table = Table(skin)
    private val dialogueText: TypingLabel

    companion object {
        private val dialogues = mapOf(
                "meeting" to DialogueMeeting(),
                "make_chapter" to DialogueMakeChapter(),
                "job_interview" to DialogueJobInterview(),
                "speech" to DialogueSpeech(),
                "letter_rec" to DialogueLetterRec()
        )

        fun getDialogueCallable(name: String): DialogueCallable {
            return dialogues.getValue(name)
        }
    }


    var component: DialogueComponent? = null

    init {
        dialogueOptionsTable.center().bottom()

        dialogueText = TypingLabel("", skin)
        dialogueTextContainer = Container(dialogueText)

        dialogueRoot = Table(skin)
        dialogueRoot.setFillParent(true)
        dialogueRoot.center().bottom()
        dialogueRoot.debug = true
        dialogueRoot.add(dialogueTextContainer)
        dialogueRoot.row()
        dialogueRoot.add(dialogueOptionsTable)

        dialogueText.typingListener = object : TypingAdapter() {
            override fun end() {
                component?.channel?.offer(ResumeDialoguePacket) ?: return
            }
        }


        stage.addActor(dialogueRoot)
    }

    private fun handleDialoguePacket(component: DialogueComponent, packet: DialoguePacket) {
        when (packet) {
            is RequestResponseDialoguePacket -> {
                dialogueOptionsTable.clear()
                val width = (stage.width - (100f * (5 - packet.options.size))) / packet.options.size
                packet.options.forEachIndexed { index, s ->
                    val label = dialogueOptionsTable.add("[${index + 1}] $s").pad(10f).width(width)
                    label.actor.setWrap(true)
                    label.actor.setFontScale(0.5f)
                    label.actor.onClickEvent { _, _ ->
                        component.waitingForResponse = false
                        component.channel.offer(ReceiveResponseDialoguePacket(index))
                    }
                }
                component.waitingForResponse = true
            }
            is SayDialoguePacket -> {
                dialogueOptionsTable.clear()
                dialogueText.setText(packet.message)
                dialogueText.restart()
            }
            GetScoreDialoguePacket -> {
                component.channel.sendBlocking(ScoreDialoguePacket(component.score))
            }
            is UpdateScoreDialoguePacket -> {
                component.score += packet.delta
                component.channel.sendBlocking(ResumeDialoguePacket)
            }
            is EndDialoguePacket -> {
                dialogueOptionsTable.clear()
                dialogueText.setText("")
            }
            else -> throw RuntimeException("Unknown packet ${packet.javaClass.name}")
        }
    }

    val keys = arrayOf(Input.Keys.NUM_1, Input.Keys.NUM_2, Input.Keys.NUM_3, Input.Keys.NUM_4, Input.Keys.NUM_5)

    private fun handleKeyPress() {
        val component = this.component ?: return
        if (!component.waitingForResponse) {
            return
        }

        keys.forEachIndexed { index, i ->
            if (Gdx.input.isKeyPressed(i)) {
                component.waitingForResponse = false
                component.channel.offer(ReceiveResponseDialoguePacket(index))
                return
            }
        }
    }


    fun update() {
        val component = this.component

        if (component == null) {
            dialogueOptionsTable.clear()
            dialogueText.setText("")
            return
        }


        handleKeyPress()
        val packet = component.channel.poll()

        if (packet != null) {
            handleDialoguePacket(component, packet)
        }
    }
}