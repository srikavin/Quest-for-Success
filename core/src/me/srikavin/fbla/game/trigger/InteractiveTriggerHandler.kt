package me.srikavin.fbla.game.trigger

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.Transform


class InteractiveTriggerHandler : TriggerHandler {
    private val font: BitmapFont

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Kenney Future.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 70
        parameter.spaceX = -10
        parameter.borderColor = Color.BLACK
        parameter.borderStraight = false
        parameter.borderWidth = 2f
        font = generator.generateFont(parameter)
        generator.dispose()
    }

    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        val positionMapper = world.getMapper(Transform::class.java)
        val camera = world.getRegistered(OrthographicCamera::class.java)
        camera.update()

        val batch = world.getRegistered(SpriteBatch::class.java)

        batch.projectionMatrix = camera.combined.cpy().scale(1 / 75f, 1 / 75f, 1f)

        if (positionMapper.has(triggerEntity)) {
            val pos = positionMapper[triggerEntity].position.cpy().sub(3f, -1f).scl(75f, 75f)

            if (batch.isDrawing) {
                font.draw(batch, "Interact [E]", pos.x, pos.y)
            } else {
                batch.begin()
                font.draw(batch, "Interact [E]", pos.x, pos.y)
                batch.end()
            }
        }

        batch.projectionMatrix = camera.combined //revert projection
    }
}
