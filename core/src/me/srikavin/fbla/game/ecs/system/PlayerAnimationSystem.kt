package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.SwitchableAnimation

/**
 * The value for the player jump animation within the graphics system
 */
const val JUMP_ANIMATION = "Jump"
/**
 * The value for the player walk animation within the graphics system
 */
const val WALK_ANIMATION = "Walk"
/**
 * The value for the player standing animation within the graphics system
 */
const val STAND_ANIMATION = "Stand"

class PlayerAnimationSystem : BaseSystem() {
    @Wire
    private lateinit var physicsBodyMapper: ComponentMapper<PhysicsBody>
    @Wire
    private lateinit var switchableAnimationMapper: ComponentMapper<SwitchableAnimation>

    override fun processSystem() {
        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

        if (physicsBodyMapper.has(player) && switchableAnimationMapper.has(player)) {
            val physicsBodyComponent = physicsBodyMapper[player]
            val switchableAnimation = switchableAnimationMapper[player]
            val vel = physicsBodyComponent.body.linearVelocity

            when {
                vel.x < -1f -> {
                    switchableAnimation.currentState = WALK_ANIMATION
                    switchableAnimation.mirror = true
                    switchableAnimation.looping = true
                }
                vel.x > 1f -> {
                    switchableAnimation.currentState = WALK_ANIMATION
                    switchableAnimation.mirror = false
                    switchableAnimation.looping = true
                }
                else -> {
                    switchableAnimation.currentState = STAND_ANIMATION
                    switchableAnimation.looping = true
                }
            }

            if (vel.y > 1f) {
                switchableAnimation.currentState = JUMP_ANIMATION
                switchableAnimation.looping = false
            }
        }
    }
}