package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.OrthographicCamera
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.SwitchableAnimation

const val JUMP_ANIMATION = "Jump"
const val Walk_ANIMATION = "Walk"
const val STAND_ANIMATION = "Stand"

class PlayerAnimationSystem(val followVertical: Boolean = false, val followHorizontal: Boolean = true) : BaseSystem() {
    lateinit var physicsBodyMapper: ComponentMapper<PhysicsBody>
    lateinit var switchableAnimationMapper: ComponentMapper<SwitchableAnimation>

    @Wire
    lateinit var camera: OrthographicCamera

    override fun processSystem() {
        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

        if (physicsBodyMapper.has(player) && switchableAnimationMapper.has(player)) {
            val physicsBodyComponent = physicsBodyMapper[player]
            val switchableAnimation = switchableAnimationMapper[player]
            val vel = physicsBodyComponent.body.linearVelocity

            if (vel.x < -1f) {
                switchableAnimation.currentState = "Walk"
                switchableAnimation.mirror = true
                switchableAnimation.looping = true
            } else if (vel.x > 1f) {
                switchableAnimation.currentState = "Walk"
                switchableAnimation.mirror = false
                switchableAnimation.looping = true
            } else {
                switchableAnimation.currentState = "Stand"
                switchableAnimation.looping = true
            }

            if (vel.y > 1f) {
                switchableAnimation.currentState = "Jump"
                switchableAnimation.looping = false
            }
        }
    }
}