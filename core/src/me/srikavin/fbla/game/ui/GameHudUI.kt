package me.srikavin.fbla.game.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.registerInputHandler
import me.srikavin.fbla.game.util.unregisterInputHandler

class GameHudUI(private val skin: Skin, private val gameState: GameState) : GameUI() {
    private val stage = Stage(ExtendViewport(1920f, 1080f, 1920f, 1080f))
    private val container = Table(skin)
    private lateinit var livesLabel: Label
    private lateinit var scoreLabel: Label

    init {
        registerInputHandler(stage)
    }

    fun build() {
        stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        container.clear()


        container.setFillParent(true)
        container.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        container.top().right()


        container.table { t ->
            gameState.awards.forEach { award ->
                val drawable = award.getDrawable()
                t.add(Image(drawable))
                        .height(100f)
                        .width(100f * 28 / 43f)
                        .padLeft(10f)
            }
        }.padRight(10f)

        container.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) { t ->
            t.defaults()

            t.add("Score: ")
            scoreLabel = t.add("0").actor
            t.row()
            t.add("Lives: ")
            livesLabel = t.add("3").actor
        }

        stage.addActor(container)
    }

    override fun render() {
        scoreLabel.setText(gameState.score)
        livesLabel.setText(gameState.lives)
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        unregisterInputHandler(stage)
    }
}