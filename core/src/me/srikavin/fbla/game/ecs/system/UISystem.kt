package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.utils.viewport.Viewport
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Transform

class UISystem : BaseSystem() {
    lateinit var stage: Stage
    lateinit var cell: Cell<Label>
    lateinit var fpsCell: Cell<Label>

    @Wire
    lateinit var tranformMapper: ComponentMapper<Transform>
    lateinit var bodyMapper: ComponentMapper<PhysicsBody>
    @Wire
    lateinit var skin: Skin

    val vector: Vector2 = Vector2()

    override fun initialize() {
        super.initialize()
        stage = Stage(ExtendViewport(640f, 480f))

        val table = Table(skin)
        table.setFillParent(true)
        table.top().left()
        stage.addActor(table)
        table.top().right()

        cell = table.add("Test asdasdasdad")
        fpsCell = table.add("Test asdasdasdad")
        cell.actor.setFontScale(0.5f)

        table.debug = true // This is optional, but enables debug lines for tables
    }

    override fun processSystem() {
        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

        cell.actor.setText("Velocty: ${bodyMapper[player].body.linearVelocity}")
        fpsCell.actor.setText("FPS: ${Gdx.graphics.framesPerSecond}")

        stage.act(Gdx.graphics.deltaTime);
        stage.draw()
    }

    override fun dispose() {
        super.dispose()
        stage.dispose()
    }
}