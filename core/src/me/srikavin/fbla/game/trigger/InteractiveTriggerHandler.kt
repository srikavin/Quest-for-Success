package me.srikavin.fbla.game.trigger

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.math.Matrix4
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.Transform


class InteractiveTriggerHandler : TriggerHandler {
    private val font: BitmapFont

    init {
        val generator = FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Kenney Future.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 100
        font = generator.generateFont(parameter)
        generator.dispose()
    }

    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        val positionMapper = world.getMapper(Transform::class.java)
        val camera = world.getRegistered(OrthographicCamera::class.java)
        camera.update()

        val batch = world.getRegistered(SpriteBatch::class.java)
        val originalMatrix: Matrix4 = camera.combined.cpy()

        batch.projectionMatrix = originalMatrix.scale(1 / 45f, 1 / 45f, 1f)

        if (positionMapper.has(triggerEntity)) {
            val pos = positionMapper[triggerEntity].position

            if (batch.isDrawing) {
                font.draw(batch, "Interact [E]", pos.x, pos.y)
            } else {
                batch.begin()
                font.draw(batch, "Interact [E]", pos.x, pos.y)
                batch.end()
            }
        }

        batch.projectionMatrix = originalMatrix //revert projection
    }
}
