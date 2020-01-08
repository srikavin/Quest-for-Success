package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World


/**
 * Responsible for drawing overlays over box2d physics objects
 */
class PhysicsDebugSystem(private var physicsWorld: World, private var debug: Boolean = true) : BaseSystem() {
    private var debugRenderer = Box2DDebugRenderer()

    @Wire
    lateinit var camera: OrthographicCamera

    override fun processSystem() {
        if (debug) {
            debugRenderer.render(physicsWorld, camera.combined)
        }
    }
}