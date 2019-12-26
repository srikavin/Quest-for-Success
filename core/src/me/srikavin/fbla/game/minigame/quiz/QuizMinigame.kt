package me.srikavin.fbla.game.minigame.quiz

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.minigame.Minigame

class QuizMinigame : Minigame {
    val inputs = GdxArray<Int>()

    init {
        inputs.add(Input.Keys.NUM_1)
        inputs.add(Input.Keys.NUM_2)
        inputs.add(Input.Keys.NUM_3)
        inputs.add(Input.Keys.NUM_4)
        inputs.add(Input.Keys.NUM_5)
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage) {

    }

    override fun isActive(): Boolean {
        return false
    }

    override fun shouldRenderBackground(): Boolean {
        return false
    }

    override fun process(delta: Float) {

    }

}