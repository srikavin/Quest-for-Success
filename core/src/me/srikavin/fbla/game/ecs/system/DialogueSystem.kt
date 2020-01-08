package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.One
import com.artemis.annotations.Wire
import com.artemis.utils.IntBag
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import kotlinx.coroutines.channels.Channel
import ktx.log.debug
import me.srikavin.fbla.game.dialogue.DialogueManager
import me.srikavin.fbla.game.ecs.component.DialogueComponent

/**
 * Handles communication between a [DialogueManager] and a [DialogueComponent] entity.
 */
@One(DialogueComponent::class)
class DialogueSystem : BaseEntitySystem() {
    @Wire
    internal lateinit var stage: Stage
    @Wire
    internal lateinit var skin: Skin
    @Wire
    private lateinit var mapper: ComponentMapper<DialogueComponent>

    private lateinit var dialogueManager: DialogueManager

    override fun initialize() {
        super.initialize()

        getSubscription().addSubscriptionListener(SubscriptionListener())

        dialogueManager = DialogueManager(stage, skin)
    }

    private inner class SubscriptionListener : EntitySubscription.SubscriptionListener {
        override fun inserted(entities: IntBag) {
            for (i in 0 until entities.size()) {
                val e = entities[i]
                if (dialogueManager.component != null) {
                    debug { "Replacing already running dialogue!" }
                }

                println(mapper[e])
                mapper[e].channel = Channel(0)
                mapper[e].script.start(mapper[e].channel)
                dialogueManager.component = mapper[e]
            }
        }

        override fun removed(entities: IntBag) {
            for (i in 0 until entities.size()) {
                val e = entities[i]

                if (mapper[e] == dialogueManager.component) {
                    dialogueManager.component = null
                }
                mapper[e].channel.close()
            }
        }
    }

    override fun processSystem() {
        dialogueManager.update()
    }

}