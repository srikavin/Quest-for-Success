package me.srikavin.fbla.game.minigame

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage

interface Minigame {
    fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage): Unit

    fun isActive(): Boolean

    fun shouldRenderBackground(): Boolean

    fun process(delta: Float)
}