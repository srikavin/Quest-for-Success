package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.log.info
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MinigameComponent


@All(MinigameComponent::class)
class MinigameRenderSystem : IteratingSystem() {
    lateinit var minigameMapper: ComponentMapper<MinigameComponent>

    @Wire
    lateinit var camera: OrthographicCamera
    @Wire
    lateinit var batch: SpriteBatch
    @Wire
    lateinit var stage: Stage
    @Wire
    lateinit var skin: Skin

    override fun initialize() {
        super.initialize()
        stage = Stage(ExtendViewport(640f, 480f))
        subscription.addSubscriptionListener(object : EntitySubscription.SubscriptionListener {
            override fun inserted(entities: IntBag) {
                for (i in 0 until entities.size()) {
                    val e: EntityInt = entities[i]
                    minigameMapper[e].minigame?.initalize(skin, stage)
                            ?: info { "Minigame not initialized upon creation: ${minigameMapper[e]}" }
                }
            }

            override fun removed(entities: IntBag) {
                // Do nothing
            }
        })
    }

    override fun process(entityId: Int) {
        val minigameComponent: MinigameComponent = minigameMapper[entityId]
        val minigame = minigameComponent.minigame ?: return

        if (minigame.active) {
            if (!minigame.shouldRenderBackground()) {
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
            }

            camera.update()
            batch.projectionMatrix = camera.combined

            minigame.render(camera, batch, stage)
        }
    }

    override fun dispose() {
        super.dispose()
        batch.dispose()
    }
}
