package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
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
private val LEFT_FORCE = Vector2(-50f, 10.0f)
private val RIGHT_FORCE = Vector2(50f, 10.0f)

@All(PlayerControlled::class, PhysicsBody::class, Transform::class)
@Exclude(DisableInput::class)
class InputSystem(private val listenerManager: ContactListenerManager) : IteratingSystem() {
    private lateinit var playerControlledMapper: ComponentMapper<PlayerControlled>
    private lateinit var physicsBodyMapper: ComponentMapper<PhysicsBody>
    private lateinit var transformMapper: ComponentMapper<Transform>

    @Wire
    lateinit var camera: OrthographicCamera
    @Wire
    lateinit var physicsWorld: World


    inner class FootContactListener : ContactListener {
        override fun endContact(contact: Contact) {
            if (contact.fixtureA.userData == player_foot_fixture_id && !contact.fixtureB.isSensor ||
                    contact.fixtureB.userData == player_foot_fixture_id && !contact.fixtureA.isSensor) {
                allowJump = false
            }
        }

        override fun beginContact(contact: Contact) {
            if (contact.fixtureA.userData == player_foot_fixture_id && !contact.fixtureB.isSensor ||
                    contact.fixtureB.userData == player_foot_fixture_id && !contact.fixtureA.isSensor) {
                allowJump = true
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

    var allowJump = true

    override fun process(entityId: Int) {
        val body = physicsBodyMapper[entityId].body

        playerControlledMapper[entityId].bindings.bindings.forEach { (action, keyCode) ->
            if (Gdx.input.isKeyPressed(keyCode)) {
                when (action) {
                    Actions.JUMP -> {
                        if (allowJump) {
                            allowJump = false
                            body.applyLinearImpulse(JUMP_IMPULSE, body.position, true)
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
                }
            }
        }

    }

}