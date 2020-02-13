package me.srikavin.fbla.game.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.scene2d.addTextTooltip
import me.srikavin.fbla.game.ext.sequence
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.registerInputHandler
import me.srikavin.fbla.game.util.unregisterInputHandler

class GameHudUI(private val skin: Skin, private val gameState: GameState) : GameUI() {
    private val stage = Stage(ExtendViewport(1920f, 1080f, 1920f, 1080f))
    private val container = Table(skin)
    private val newAwardContainer = Table(skin)
    private lateinit var livesLabel: Label
    private lateinit var scoreLabel: Label
    private lateinit var awardsTable: Table

    private var awardsHashCode = -1

    init {
        registerInputHandler(stage)
    }

    fun build() {
        newAwardContainer.clear()
        newAwardContainer.setFillParent(true)
        newAwardContainer.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        stage.addActor(newAwardContainer)

        container.clear()
        container.setFillParent(true)
        container.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        container.top().right()
        container.table {
            it.top().right()
            awardsTable = it.table().padRight(10f).actor

            it.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) { t ->
                t.defaults()

                t.add("Score: ")
                scoreLabel = t.add("0").actor
                t.row()
                t.add("Lives: ")
                livesLabel = t.add("3").actor
            }
        }

        stage.addActor(container)
    }

    fun renderAwards() {
        awardsHashCode = gameState.awards.hashCode()
        gameState.awards.forEach { award ->
            val drawable = award.getDrawable()
            awardsTable.add(Image(drawable).apply {
                        val manager = TooltipManager.getInstance()
                        manager.maxWidth = 350f
                        manager.instant()
                        manager.initialTime = 0.35f
                        addTextTooltip(award.getDescription(), skin = skin, tooltipManager = manager)
                    })
                    .height(100f)
                    .width(100f * 28 / 43f)
                    .padLeft(10f)
        }
    }

    override fun render() {
        stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height)

        scoreLabel.setText(gameState.score)
        livesLabel.setText(gameState.lives)

        val last = gameState.lastAddedAward
        if (last != null) {
            gameState.lastAddedAward = null
            awardsTable.clear()

            newAwardContainer.clear()

            newAwardContainer.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) {
                it.add(Image(last.getDrawable())).height(200f).width(200f * 28 / 43f)
                it.row()
                it.add(Label(last.getDescription(), skin, "menu").apply {
                    setWrap(true)
                }).width(550f).pad(15f)

                it.sequence(
                        Actions.fadeIn(0.5f),
                        Actions.fadeIn(8f),
                        Actions.fadeOut(0.75f),
                        Actions.removeActor()
                )

                renderAwards()
            }
        }

        if (awardsHashCode != gameState.awards.hashCode()) {
            renderAwards()
        }

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        unregisterInputHandler(stage)
    }
}