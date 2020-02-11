package me.srikavin.fbla.game.trigger

import com.artemis.World
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ext.sequence
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.EntityInt
import me.srikavin.fbla.game.util.GameFonts


class LevelTitleTriggerHandler : TriggerHandler {
    lateinit var skin: Skin
    val stage = Stage(ExtendViewport(1920f, 1080f))

    var initialized = false

    lateinit var container: Table
    lateinit var infoPanel: Table

    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        val state = world.getRegistered(GameState::class.java)
        val message = (trigger.properties["message"] as String).replace("{score}", "${state.score}")
        infoPanel.clear()
        val act = infoPanel.add(message).actor

        if (message.count { c -> c == '\n' } == 0) {
            act.style.font = GameFonts.LARGE
        }

        infoPanel.isVisible = true
        infoPanel.sequence(
                Actions.fadeIn(0.5f),
                Actions.fadeIn(1.5f),
                Actions.fadeOut(0.5f),
                Actions.hide()
        )
    }

    fun tick(world: World) {
        if (!initialized) {
            initialized = true

            val skin = world.getRegistered(Skin::class.java)

            container = Table(skin)
            container.setFillParent(true)
            container.setSize(1920f, 1080f)
            stage.addActor(container)

            infoPanel = container.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) {
            }.width(940f).height(200f).fill().actor
        }

        stage.act()
        stage.draw()
    }
}
