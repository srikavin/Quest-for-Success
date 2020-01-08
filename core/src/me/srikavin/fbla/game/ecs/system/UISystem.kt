package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.annotations.Wire
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.rafaskoberg.gdx.typinglabel.TypingConfig
import me.srikavin.fbla.game.GameState

/**
 * Responsible for drawing and updating UI elements
 */
class UISystem : BaseSystem() {
    @Wire
    private lateinit var stage: Stage
    @Wire
    private lateinit var root: Table
    @Wire
    private lateinit var gameState: GameState

    private lateinit var scoreCell: Cell<Label>
    private lateinit var livesCell: Cell<Label>

    override fun initialize() {
        super.initialize()
        TypingConfig.DEFAULT_SPEED_PER_CHAR = 0.05f

        Gdx.input.inputProcessor = stage

        scoreCell = root.add("")
        livesCell = root.add("")
    }

    override fun processSystem() {
        scoreCell.actor.setText("Score: ${gameState.score}")
        livesCell.actor.setText("Lives: ${gameState.lives}")

        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun dispose() {
        super.dispose()
        stage.dispose()
    }
}