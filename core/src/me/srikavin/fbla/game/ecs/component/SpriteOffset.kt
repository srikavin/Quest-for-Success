package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

/**
 * Component containing the offset from the entity's position ([Transform]) and the sprite's position
 */
class SpriteOffset() : Component() {
    constructor(offset: Vector2) : this() {
        this.offset = offset
    }

    lateinit var offset: Vector2
}