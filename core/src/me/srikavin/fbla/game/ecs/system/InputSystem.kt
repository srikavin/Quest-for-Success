package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import ktx.log.info
import me.srikavin.fbla.game.Actions
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.PlayerControlled
import me.srikavin.fbla.game.ecs.component.Transform
import java.util.function.BiConsumer

@All(PlayerControlled::class, PhysicsBody::class, Transform::class)
class InputSystem : IteratingSystem() {
    lateinit var playerControlledMapper: ComponentMapper<PlayerControlled>
    lateinit var physicsBodyMapper: ComponentMapper<PhysicsBody>
    lateinit var transformMapper: ComponentMapper<Transform>

    @Wire
    lateinit var camera: OrthographicCamera

    val JUMP_IMPULSE = Vector2(0.0f, 1.0f)
    val LEFT_IMPULSE = Vector2(-0.5f, 0.0f)
    val RIGHT_IMPULSE = Vector2(0.5f, 0.0f)

    override fun process(entityId: Int) {
        val body = physicsBodyMapper[entityId].body
        val position = transformMapper[entityId].position

        playerControlledMapper[entityId].bindings.bindings.forEach(BiConsumer { action, keyCode ->
            if (Gdx.input.isKeyPressed(keyCode)) {
                when (action) {
                    Actions.JUMP -> body.applyLinearImpulse(JUMP_IMPULSE, position, true)
                    Actions.MOVE_LEFT -> body.applyLinearImpulse(LEFT_IMPULSE, position, true)
                    Actions.MOVE_RIGHT -> body.applyLinearImpulse(RIGHT_IMPULSE, position, true)
                    Actions.USE -> TODO()
                }
            }
        })

    }

}