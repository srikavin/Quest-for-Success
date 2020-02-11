package me.srikavin.fbla.game.minigame.quiz

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import me.srikavin.fbla.game.minigame.Minigame
import me.srikavin.fbla.game.util.GdxArray

class QuizMinigame : Minigame() {
    private val inputs = GdxArray<Int>()

    init {
        inputs.add(Input.Keys.NUM_1)
        inputs.add(Input.Keys.NUM_2)
        inputs.add(Input.Keys.NUM_3)
        inputs.add(Input.Keys.NUM_4)
        inputs.add(Input.Keys.NUM_5)
    }

    override fun resetMinigame(properties: MapProperties) {

    }

    override fun initializeMinigame(skin: Skin, stage: Stage) {
        stage.root = Table(skin)
        stage.root.addActor(TypingLabel("Text", skin))
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage) {

    }

    override fun shouldRenderBackground(): Boolean {
        return false
    }

    override fun process(delta: Float) {

    }

}