package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseEntitySystem
import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.*
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.physics.ContactListenerManager
import me.srikavin.fbla.game.trigger.TriggerManager


@All(MapTrigger::class, PhysicsBody::class)
class TriggerSystem(private val listenerManager: ContactListenerManager) : BaseEntitySystem() {
    private lateinit var triggerMapper: ComponentMapper<MapTrigger>
    private lateinit var physicsMapper: ComponentMapper<PhysicsBody>

    data class TriggerEvent(val player: EntityInt, val other: EntityInt)

    private val triggerManager = TriggerManager()

    private val collisions: MutableSet<TriggerEvent> = HashSet()

    @Wire
    lateinit var camera: OrthographicCamera

    inner class CollisionListener : ContactListener {
        override fun endContact(contact: Contact) {
            val playerId = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

            val other: Fixture = when {
                contact.fixtureA.userData == playerId -> {
                    contact.fixtureB
                }
                contact.fixtureB.userData == playerId -> {
                    contact.fixtureA
                }
                else -> {
                    // Does not involve player
                    return
                }
            }

            val e: EntityInt = other.userData as Int
            collisions.remove(TriggerEvent(playerId, e))
        }

        override fun beginContact(contact: Contact) {
            val playerId = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

            val other = when {
                contact.fixtureA.userData == playerId -> {
                    contact.fixtureB
                }
                contact.fixtureB.userData == playerId -> {
                    contact.fixtureA
                }
                else -> {
                    // Does not involve player
                    return
                }
            }

            val e: EntityInt = other.userData as Int
            if (triggerMapper.has(e)) {
                collisions.add(TriggerEvent(playerId, e))
            }

        }

        override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        }

        override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        }
    }


    override fun initialize() {
        super.initialize()
        listenerManager.addListener(CollisionListener())
    }

    override fun processSystem() {
        collisions.forEach {
            if (triggerMapper.has(it.other)) {
                triggerManager.handle(world, it.player, it.other, triggerMapper[it.other])
            }
        }
    }
}
