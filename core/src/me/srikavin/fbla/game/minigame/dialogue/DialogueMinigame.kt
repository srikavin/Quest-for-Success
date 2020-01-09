package me.srikavin.fbla.game.minigame.dialogue

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import kotlinx.coroutines.channels.Channel
import me.srikavin.fbla.game.GameState
import me.srikavin.fbla.game.dialogue.DialogueCallable
import me.srikavin.fbla.game.dialogue.DialogueManager
import me.srikavin.fbla.game.ecs.component.DialogueComponent
import me.srikavin.fbla.game.minigame.Minigame

class DialogueMinigame : Minigame() {
    lateinit var dialogueManager: DialogueManager
    lateinit var dialogueCallable: DialogueCallable

    override fun resetMinigame(properties: MapProperties) {
        dialogueCallable = DialogueManager.getDialogueCallable(this.mapProperties.subtype)
    }

    override fun initializeMinigame(skin: Skin, stage: Stage) {
        dialogueManager = DialogueManager(stage, skin)
        dialogueManager.component = DialogueComponent().apply {
            this.channel = Channel(0)
            this.script = dialogueCallable
            dialogueCallable.start(channel)
        }
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage) {
        dialogueManager.update()

        if (dialogueManager.component?.finished == true) {
            world.getRegistered(GameState::class.java).score += dialogueManager.component?.score ?: 0
            this.endMinigame()
        }
    }

    override fun shouldRenderBackground(): Boolean {
        return false
    }

    override fun process(delta: Float) {

    }

}