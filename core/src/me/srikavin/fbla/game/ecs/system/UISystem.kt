package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.rafaskoberg.gdx.typinglabel.TypingConfig
import me.srikavin.fbla.game.ecs.component.Dead
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.ui.DeadUI
import me.srikavin.fbla.game.ui.GameHudUI

/**
 * Responsible for drawing and updating UI elements
 */
class UISystem : BaseSystem() {
    private lateinit var gameHudUI: GameHudUI
    private lateinit var deadUI: DeadUI

    var showingDead = false

    @Wire
    private lateinit var deadMapper: ComponentMapper<Dead>

    override fun initialize() {
        super.initialize()
        TypingConfig.DEFAULT_SPEED_PER_CHAR = 0.05f

        gameHudUI = GameHudUI(world.getRegistered(Skin::class.java), world.getRegistered(GameState::class.java))
        deadUI = DeadUI(world.getRegistered(Skin::class.java))

        gameHudUI.build()
    }

    override fun processSystem() {
        gameHudUI.render()

        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

        showingDead = if (deadMapper.has(player)) {
            if (!showingDead) {
                deadUI.build(deadMapper[player])
            }
            true
        } else {
            false
        }

        if (showingDead) {
            deadUI.render()
        }
    }

    override fun dispose() {
        super.dispose()
        gameHudUI.dispose()
        deadUI.dispose()
    }
}