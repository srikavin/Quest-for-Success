package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.Entity
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.EntityProcessingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World
import me.srikavin.fbla.game.ecs.component.PhysicsBody


/**
 * Responsible for drawing overlays over box2d physics objects
 */
@All(PhysicsBody::class)
class PhysicsDebugSystem(private var physicsWorld: World, var debug: Boolean = true) : EntityProcessingSystem() {
    private var debugRenderer = Box2DDebugRenderer(true, true, false, true, true, true)

    @Wire
    lateinit var camera: OrthographicCamera

    @Wire
    lateinit var physicsMapper: ComponentMapper<PhysicsBody>

    lateinit var batch: SpriteBatch


    override fun initialize() {
        super.initialize()
        batch = SpriteBatch()
    }

    override fun process(e: Entity?) {
    }

    override fun begin() {
        super.begin()
//        batch.begin()
        if (debug) {
            debugRenderer.render(physicsWorld, camera.combined)
//            batch.projectionMatrix = camera.combined
        }
    }

}