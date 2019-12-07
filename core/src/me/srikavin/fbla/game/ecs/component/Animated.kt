package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Animated() : Component() {
    lateinit var animation: Animation<TextureRegion>
    var looping: Boolean = false
}