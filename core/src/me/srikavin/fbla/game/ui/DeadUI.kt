package me.srikavin.fbla.game.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.srikavin.fbla.game.ecs.component.Dead
import me.srikavin.fbla.game.ext.addImageTextButton
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.SaveUtils
import me.srikavin.fbla.game.util.registerInputHandler
import me.srikavin.fbla.game.util.unregisterInputHandler

class DeadUI(private val skin: Skin, private val gameState: GameState) : GameUI() {
    private val stage = Stage(ExtendViewport(1920f, 1080f, 1920f, 1080f))
    private val container = Table(skin)

    private var deathMessages = listOf(
            "Ouch! That must’ve hurt! Try again?",
            "Dang! What happened?! Try again?",
            "Oof! Wah! wah! wah! Try again?"
    )

    init {
        registerInputHandler(stage)
    }

    private fun getRandomDeathString(): String {
        val rand = MathUtils.random(0, deathMessages.size - 1)
        return deathMessages[rand]
    }

    fun build(deadComponent: Dead) {
        stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height)
        container.clear()

        container.setFillParent(true)
        container.setSize(1920f, 1080f)
        stage.addActor(container)

        container.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) {
            it.table { inner ->
                inner.add(getRandomDeathString())
            }
            it.row()
            it.add().height(10f)
            it.row()
            it.table { inner ->
                inner.addImageTextButton("[green]Restart Level[]", null, Runnable {
                    deadComponent.respawnRunnable()
                }, "menu")
                inner.add().width(45f)
                inner.addImageTextButton("[accent]Save and Quit[]", null, Runnable {
                    SaveUtils.saveGame(gameState)
                    Gdx.app.exit()
                }, "menu")
            }
        }.width(1040f).height(150f).fill()
    }

    override fun render() {
        stage.act()
        stage.draw()
    }

    override fun dispose() {
        unregisterInputHandler(stage)
    }
}