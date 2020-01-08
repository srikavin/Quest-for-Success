package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold
import me.srikavin.fbla.game.Actions
import me.srikavin.fbla.game.ecs.component.DisableInput
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.PlayerControlled
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.graphics.player_foot_fixture_id
import me.srikavin.fbla.game.physics.ContactListenerManager

private const val JUMP_DELAY_SEC = 1.5f
private const val MAX_HORIZONTAL_VELOCITY = 7f
private val JUMP_IMPULSE = Vector2(0.0f, 25.0f)
private val LEFT_FORCE = Vector2(-50f, 0.0f)
private val RIGHT_FORCE = Vector2(50f, 0.0f)

@All(PlayerControlled::class, PhysicsBody::class, Transform::class)
@Exclude(DisableInput::class)
class InputSystem(private val listenerManager: ContactListenerManager) : IteratingSystem() {
    private lateinit var playerControlledMapper: ComponentMapper<PlayerControlled>
    private lateinit var physicsBodyMapper: ComponentMapper<PhysicsBody>

    @Wire
    lateinit var camera: OrthographicCamera

    inner class FootContactListener : ContactListener {
        override fun endContact(contact: Contact) {
            if (contact.fixtureA.userData == player_foot_fixture_id && !contact.fixtureB.isSensor ||
                    contact.fixtureB.userData == player_foot_fixture_id && !contact.fixtureA.isSensor) {
                allowJump -= 1
                if (allowJump < 0) {
                    allowJump = 0
                }
            }
        }

        override fun beginContact(contact: Contact) {
            if (contact.fixtureA.userData == player_foot_fixture_id && !contact.fixtureB.isSensor ||
                    contact.fixtureB.userData == player_foot_fixture_id && !contact.fixtureA.isSensor) {
                allowJump += 1
            }
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {
        }

        override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        }
    }

    override fun initialize() {
        super.initialize()
        listenerManager.addListener(FootContactListener())
    }

    var allowJump: Int = 0

    override fun process(entityId: Int) {
        val body = physicsBodyMapper[entityId].body

        playerControlledMapper[entityId].bindings.bindings.forEach { (action, keyCode) ->
            if (Gdx.input.isKeyPressed(keyCode)) {
                when (action) {
                    Actions.JUMP -> {
                        if (allowJump > 0) {
                            body.applyLinearImpulse(JUMP_IMPULSE, body.position, true)
                            allowJump = 0
                        }
                    }
                    Actions.MOVE_LEFT -> {
                        if (body.linearVelocity.x > -MAX_HORIZONTAL_VELOCITY) {
                            body.applyForceToCenter(LEFT_FORCE, true)
                        }
                    }
                    Actions.MOVE_RIGHT -> {
                        if (body.linearVelocity.x < MAX_HORIZONTAL_VELOCITY) {
                            body.applyForceToCenter(RIGHT_FORCE, true)
                        }
                    }
                    Actions.USE -> TODO()
                    Actions.QUIT -> {
                        Gdx.app.exit()
                    }
                }
            }
        }

    }
}