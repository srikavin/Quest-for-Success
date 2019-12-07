package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2

class SpriteOffset() : Component() {
    constructor(offset: Vector2) : this() {
        this.offset = offset
    }

    lateinit var offset: Vector2
}