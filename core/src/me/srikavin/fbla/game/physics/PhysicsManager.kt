package me.srikavin.fbla.game.physics

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import ktx.box2d.createWorld
import ktx.box2d.earthGravity


class PhysicsManager {
    val world = createWorld(gravity = earthGravity)
    var debugRenderer = Box2DDebugRenderer()

}