package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import me.srikavin.fbla.game.Actions
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.PlayerControlled
import me.srikavin.fbla.game.ecs.component.Transform
import java.util.function.BiConsumer

private const val JUMP_DELAY_SEC = 1.5f
private const val MAX_HORIZONTAL_VELOCITY = 7f
private val JUMP_IMPULSE = Vector2(0.0f, 25.0f)
private val LEFT_FORCE = Vector2(-50f, 10.0f)
private val RIGHT_FORCE = Vector2(50f, 10.0f)

@All(PlayerControlled::class, PhysicsBody::class, Transform::class)
class InputSystem : IteratingSystem() {
    lateinit var playerControlledMapper: ComponentMapper<PlayerControlled>
    lateinit var physicsBodyMapper: ComponentMapper<PhysicsBody>
    lateinit var transformMapper: ComponentMapper<Transform>

    @Wire
    lateinit var camera: OrthographicCamera


    var jumpDelay = 0f

    override fun process(entityId: Int) {
        val body = physicsBodyMapper[entityId].body
        val position = transformMapper[entityId].position

        jumpDelay -= Gdx.graphics.deltaTime

        playerControlledMapper[entityId].bindings.bindings.forEach(BiConsumer { action, keyCode ->
            if (Gdx.input.isKeyPressed(keyCode)) {
                when (action) {
                    Actions.JUMP -> {
                        if (jumpDelay < 0) {
                            jumpDelay = JUMP_DELAY_SEC
                        } else {
                            return@BiConsumer
                        }
                        body.applyLinearImpulse(JUMP_IMPULSE, body.position, true)
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
        })

    }

}