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
    private lateinit var _currentState: String
    var currentState: String
        get() = _currentState
        set(value) {
            if (locked <= 0) {
                _currentState = value
            }
        }
    var mirror: Boolean = false
    var looping: Boolean = false
    private var locked: Float = 0f

    /**
     * Lock this animation component to the given animation. Multiple calls to [lock] will override the previous ones.
     *
     * @param animationName The name of the animation to lock to.
     */
    fun lock(animationName: String) {
        locked = animations[animationName].animationDuration
        _currentState = animationName
    }

    fun getCurrentAndUpdate(delta: Float): Animation<TextureRegion> {
        locked -= delta
        return animations[currentState]
    }

}