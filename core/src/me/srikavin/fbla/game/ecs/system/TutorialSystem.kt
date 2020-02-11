package me.srikavin.fbla.game.ecs.system

import com.artemis.Entity
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.EntityProcessingSystem
import com.badlogic.gdx.scenes.scene2d.Stage
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.physics.ContactListenerManager
import me.srikavin.fbla.game.util.EntityInt

/**
 * Responsible for handling tutorial components
 */
@All(MapTrigger::class, PhysicsBody::class)
class TutorialSystem(private val listenerManager: ContactListenerManager) : EntityProcessingSystem() {
    private data class TriggerEvent(val player: EntityInt, val other: EntityInt)

    @Wire
    lateinit var stage: Stage

    override fun process(e: Entity?) {

    }
}
