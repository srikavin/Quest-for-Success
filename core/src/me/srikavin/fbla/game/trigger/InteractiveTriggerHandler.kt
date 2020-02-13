package me.srikavin.fbla.game.trigger

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.util.EntityInt
import me.srikavin.fbla.game.util.GameFonts


class InteractiveTriggerHandler : TriggerHandler {
    private val tempMatrix: Matrix4 = Matrix4()
    private val tempVector2: Vector2 = Vector2()

    lateinit var skin: Skin
    private val dialog by lazy {
        object : Dialog("Hint", skin) {
            override fun result(obj: Any?) {
                this.remove()
            }
        }.apply {
            titleLabel.setFillParent(true)
            button("Close")
        }
    }


    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        val positionMapper = world.getMapper(Transform::class.java)
        val camera = world.getRegistered(OrthographicCamera::class.java)
        camera.update()

        val batch = world.getRegistered(SpriteBatch::class.java)

        batch.projectionMatrix = tempMatrix.set(camera.combined).scale(1 / 75f, 1 / 75f, 1f)

        if (positionMapper.has(triggerEntity)) {
            val pos = tempVector2.set(positionMapper[triggerEntity].position).sub(3f, -1f).scl(75f, 75f)

            if (batch.isDrawing) {
                GameFonts.HUGE.draw(batch, "Interact [E]", pos.x, pos.y)
            } else {
                batch.begin()
                GameFonts.HUGE.draw(batch, "Interact [E]", pos.x, pos.y)
                batch.end()
            }
        }

        batch.projectionMatrix = camera.combined //revert projection

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            skin = world.getRegistered(Skin::class.java)
            val stage = world.getRegistered(Stage::class.java)
            dialog.contentTable.clear()
            dialog.text(Label("\n" + trigger.properties["message"], skin, "black").apply {
                this.setFontScale(.75f)
            })

            dialog.show(stage)
        }
    }
}
