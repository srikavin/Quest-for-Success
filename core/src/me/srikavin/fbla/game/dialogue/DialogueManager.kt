package me.srikavin.fbla.game.dialogue

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.Align
import com.rafaskoberg.gdx.typinglabel.TypingAdapter
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import kotlinx.coroutines.channels.sendBlocking
import me.srikavin.fbla.game.dialogue.callable.*
import me.srikavin.fbla.game.dialogue.quiz.QuizFBLAKnowledge
import me.srikavin.fbla.game.ecs.component.DialogueComponent
import me.srikavin.fbla.game.ext.addImageTextButton
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.util.GameFonts

/**
 * Managers communications with [DialogueCallable]s and [me.srikavin.fbla.game.ecs.system.DialogueSystem] and handles
 * updating UI drawn to the screen with dialogue text and options.
 */
class DialogueManager(private val stage: Stage, skin: Skin) {
    private val keys = arrayOf(Input.Keys.NUM_1, Input.Keys.NUM_2, Input.Keys.NUM_3, Input.Keys.NUM_4, Input.Keys.NUM_5)

    private val dialogueRoot: Table
    private val dialogueTextContainer: Container<TypingLabel>
    private val dialogueOptionsTable: Table = Table(skin)
    private val dialogueText: TypingLabel

    var component: DialogueComponent? = null

    companion object {

        private val dialogues = mapOf(
                "meeting" to DialogueMeeting(),
                "recruit" to DialogueMakeChapter(),
                "job_interview" to DialogueJobInterview(),
                "office" to DialogueSpeech(),
                "letter_rec" to DialogueLetterRec(),
                "fbla_knowledge" to QuizFBLAKnowledge(),
                "animal" to DialogueAnimal(),
                "brochure" to DialogueBrochure(),
                "social_media" to DialogueSocialMedia(),
                "blog" to DialogueBlog()
        )

        /**
         * Gets the [DialogueCallable] associated with a given name
         *
         * @param name The name of the Dialogue Callable to lookup
         */
        fun getDialogueCallable(name: String): DialogueCallable {
            return dialogues.getValue(name)
        }
    }

    init {
        dialogueOptionsTable.center().bottom()
        dialogueText = TypingLabel("", skin)
        dialogueText.setWrap(true)
        dialogueText.style.font = GameFonts.DEFAULT
        dialogueText.setAlignment(Align.center, Align.center)
        dialogueTextContainer = Container(dialogueText).width(1000f)

        dialogueRoot = Table(skin)
        dialogueRoot.setFillParent(true)

        dialogueRoot.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) { table ->
            table.center().bottom()
            table.add(dialogueTextContainer)
            table.row()
            table.add(dialogueOptionsTable)
        }.pad(10f)

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
                packet.options.forEachIndexed { index, s ->
                    val button = dialogueOptionsTable.addImageTextButton("[#d3d3d3][${index + 1}][] $s", null, Runnable {
                        component.waitingForResponse = false
                        component.channel.offer(ReceiveResponseDialoguePacket(index))
                    }, "menu").actor
                    button.label.setWrap(true)
                    button.labelCell.width(250f)
                }
                component.waitingForResponse = true
            }
            is SayDialoguePacket -> {
                dialogueOptionsTable.clear()
                dialogueText.restart(packet.message)
                dialogueText.invalidateHierarchy()
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
                component.finished = true
            }
            else -> throw RuntimeException("Unknown packet ${packet.javaClass.name}")
        }
    }


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


    /**
     * Updates the state of the dialogue and polls for any incoming packets.
     * This should be called within the game loop.
     */
    fun update() {
        val component = this.component

        if (component == null) {
            dialogueOptionsTable.clear()
            dialogueText.setText("")
            dialogueRoot.isVisible = false
            return
        }

        dialogueRoot.isVisible = true

        handleKeyPress()
        val packet = component.channel.poll()

        if (packet != null) {
            handleDialoguePacket(component, packet)
        }
    }
}