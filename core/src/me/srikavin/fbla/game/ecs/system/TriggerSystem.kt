package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.*
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.physics.ContactListenerManager
import me.srikavin.fbla.game.trigger.TriggerManager


@All(MapTrigger::class, PhysicsBody::class)
class TriggerSystem(private val listenerManager: ContactListenerManager) : IteratingSystem() {
    private lateinit var triggerMapper: ComponentMapper<MapTrigger>
    private lateinit var physicsMapper: ComponentMapper<PhysicsBody>

    private val triggerManager = TriggerManager()

    @Wire
    lateinit var camera: OrthographicCamera

    inner class CollisionListener : ContactListener {
        override fun endContact(contact: Contact?) {
        }

        override fun beginContact(contact: Contact) {
            val playerId = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

            val player: Fixture
            val other: Fixture

            when {
                contact.fixtureA.userData == playerId -> {
                    player = contact.fixtureA
                    other = contact.fixtureB
                }
                contact.fixtureB.userData == playerId -> {
                    player = contact.fixtureB
                    other = contact.fixtureA
                }
                else -> {
                    // Does not involve player
                    return
                }
            }

            val e: EntityInt = other.userData as Int
            if (triggerMapper.has(e)) {
                val trigger = triggerMapper[e]

                // Handle outside of physics simulation
                Gdx.app.postRunnable {
                    triggerManager.handle(world, e, other.userData as Int, trigger)
                }
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

    override fun begin() {
    }

    override fun process(entityId: Int) {
    }
}
