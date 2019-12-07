package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World


class PhysicsDebugSystem(var physicsWorld: World) : BaseSystem() {
    private var debugRenderer = Box2DDebugRenderer()

    @Wire
    lateinit var camera: OrthographicCamera

    override fun processSystem() {
        debugRenderer.render(physicsWorld, camera.combined)
    }
}