package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import me.srikavin.fbla.game.ecs.component.MinigameComponent


@All(MinigameComponent::class)
class MinigameRenderSystem : IteratingSystem() {
    lateinit var minigameMapper: ComponentMapper<MinigameComponent>

    @Wire
    lateinit var camera: OrthographicCamera
    @Wire
    lateinit var batch: SpriteBatch

    override fun process(entityId: Int) {
        val minigameComponent: MinigameComponent = minigameMapper[entityId]
        val minigame = minigameComponent.minigame ?: return

        if (minigame.isActive()) {
            if (!minigame.shouldRenderBackground()) {
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            }

            camera.update()
            batch.projectionMatrix = camera.combined

//            minigame.render(camera, batch, stage )
        }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}
