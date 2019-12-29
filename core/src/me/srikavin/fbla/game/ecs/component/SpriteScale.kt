package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class SpriteScale() : Component() {
    constructor(scale: Vector2) : this() {
        this.scale = scale
    }

    constructor(scale: Float) : this() {
        this.scale = Vector2(scale, scale)
    }

    lateinit var scale: Vector2
}