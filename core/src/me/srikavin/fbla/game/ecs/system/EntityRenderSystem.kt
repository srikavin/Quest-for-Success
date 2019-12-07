package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Exclude
import com.artemis.annotations.One
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import ktx.log.info
import me.srikavin.fbla.game.ecs.component.*
import me.srikavin.fbla.game.graphics.scale_factor

@One(Animated::class, Sprite::class)
@All(Transform::class)
@Exclude(Background::class)
class EntityRenderSystem : IteratingSystem() {
    private lateinit var spriteMapper: ComponentMapper<Sprite>
    private lateinit var animatedMapper: ComponentMapper<Animated>
    private lateinit var transformMapper: ComponentMapper<Transform>
    private lateinit var offsetMapper: ComponentMapper<SpriteOffset>
    private val recycledPosition = Vector2()

    @Wire
    lateinit var batch: SpriteBatch

    private var stateTime = 0f;


    override fun begin() {
        stateTime += Gdx.graphics.deltaTime;
        batch.begin()
    }

    override fun process(entityId: Int) {
        recycledPosition.set(transformMapper[entityId].position)

        val sprite: TextureRegion = when {
            spriteMapper.has(entityId) -> spriteMapper[entityId].sprite
            animatedMapper.has(entityId) -> {
                val animated = animatedMapper[entityId]
                animated.animation.getKeyFrame(stateTime, animated.looping)
            }
            else -> throw RuntimeException("Entity does not have Sprite nor Animated; should never happen")
        }



        if (offsetMapper.has(entityId)){
            recycledPosition.add(offsetMapper[entityId].offset)
        }

        batch.draw(sprite, recycledPosition.x, recycledPosition.y, sprite.regionWidth * scale_factor, sprite.regionHeight * scale_factor)
    }

    override fun end() {
        batch.end()
    }
}
