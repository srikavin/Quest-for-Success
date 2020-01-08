package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import ktx.collections.GdxMap

/**
 * Component containing mapping between entity states and animations
 */
class SwitchableAnimation : Component() {
    lateinit var animations: GdxMap<String, Animation<TextureRegion>>
    lateinit var currentState: String
    var mirror: Boolean = false
    var looping: Boolean = false
}