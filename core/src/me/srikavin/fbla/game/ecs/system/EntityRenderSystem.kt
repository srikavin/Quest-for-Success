package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.annotations.One
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import me.srikavin.fbla.game.ecs.component.*
import me.srikavin.fbla.game.graphics.scale_factor

@One(Animated::class, Sprite::class, SwitchableAnimation::class)
@All(Transform::class)
@Exclude(Background::class)
class EntityRenderSystem : IteratingSystem() {
    private lateinit var spriteMapper: ComponentMapper<Sprite>
    private lateinit var animatedMapper: ComponentMapper<Animated>
    private lateinit var switchableAnimationMapper: ComponentMapper<SwitchableAnimation>
    private lateinit var transformMapper: ComponentMapper<Transform>
    private lateinit var offsetMapper: ComponentMapper<SpriteOffset>
    private val recycledPosition = Vector2()

    @Wire
    lateinit var batch: SpriteBatch

    private var stateTime = 0f


    override fun begin() {
        stateTime += Gdx.graphics.deltaTime
        batch.begin()
    }

    override fun process(entityId: Int) {
        recycledPosition.set(transformMapper[entityId].position)

        var mirrored: Boolean = false

        val sprite: TextureRegion = when {
            spriteMapper.has(entityId) -> spriteMapper[entityId].sprite
            switchableAnimationMapper.has(entityId) -> {
                val animated = switchableAnimationMapper[entityId]
                mirrored = animated.mirror
                animated.animations[animated.currentState].getKeyFrame(stateTime, animated.looping)
            }
            animatedMapper.has(entityId) -> {
                val animated = animatedMapper[entityId]
                animated.animation.getKeyFrame(stateTime, animated.looping)
            }
            else -> throw RuntimeException("Entity does not have Sprite nor Animated; should never happen")
        }



        if (offsetMapper.has(entityId)) {
            recycledPosition.add(offsetMapper[entityId].offset)
        }

        val w = sprite.regionWidth * scale_factor
        val h = sprite.regionHeight * scale_factor


        if (mirrored) {
            batch.draw(sprite, recycledPosition.x + w, recycledPosition.y, -w, h)
        } else {
            batch.draw(sprite, recycledPosition.x, recycledPosition.y, w, h)
        }
    }

    override fun end() {
        batch.end()
    }
}
