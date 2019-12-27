package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.rafaskoberg.gdx.typinglabel.TypingConfig
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Transform

class UISystem : BaseSystem() {
    @Wire
    lateinit var stage: Stage
    @Wire
    lateinit var root: Table
    lateinit var fpsCell: Cell<Label>

    @Wire
    lateinit var tranformMapper: ComponentMapper<Transform>
    lateinit var bodyMapper: ComponentMapper<PhysicsBody>
    @Wire
    lateinit var skin: Skin

    override fun initialize() {
        super.initialize()
        TypingConfig.DEFAULT_SPEED_PER_CHAR = 0.05f

        fpsCell = root.add("60")
    }

    override fun processSystem() {
        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")


//        cell.actor.setText("Velocty: ${bodyMapper[player].body.linearVelocity}")
        fpsCell.actor.setText("Position: ${tranformMapper[player].position}")

        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    override fun dispose() {
        super.dispose()
        stage.dispose()
    }
}