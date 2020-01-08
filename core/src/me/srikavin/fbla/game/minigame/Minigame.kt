package me.srikavin.fbla.game.minigame

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin

abstract class Minigame {
    var active: Boolean = false

    /**
     * Reset the minigame to its initial conditions. This will always be called before a minigame is made active.
     */
    abstract fun reset(properties: MapProperties)

    /**
     * Any inital UI initialization should occur here. The stage will not be modified outside of the minigame while it
     * is active.
     */
    abstract fun initalize(skin: Skin, stage: Stage)

    /**
     * The stage will not be modified outside of the minigame while the minigame remains active. No references to the
     * stage should be kept within the minigame as they may be reused in other minigames after being cleared.
     */
    abstract fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage)

    /**
     * If true, the screen will not be cleared before [render] is called, if false the screen will be cleared before
     * [render] is called.
     */
    abstract fun shouldRenderBackground(): Boolean

    /**
     * Any updates to the minigame should be processed here.
     *
     * @param delta The time that has passed since the last call to this function
     */
    abstract fun process(delta: Float)

    /**
     * If true, input will still affect the player. If false, the player movement cannot be affected while the minigame
     * is active. This value should remain constant after initialization.
     */
    open fun allowPlayerMovement(): Boolean {
        return false
    }
}