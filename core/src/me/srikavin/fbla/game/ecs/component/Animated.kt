package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion

/**
 * Component indicating that an entity has a animated sprite rather than a static image
 */
class Animated : Component() {
    lateinit var animation: Animation<TextureRegion>
    var looping: Boolean = false
}