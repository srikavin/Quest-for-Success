package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import me.srikavin.fbla.game.ecs.component.Map
import me.srikavin.fbla.game.ecs.component.Transform


@All(Map::class, Transform::class)
class BackgroundRenderSystem : IteratingSystem() {
    private lateinit var mapMapper: ComponentMapper<Map>
    private lateinit var transformMapper: ComponentMapper<Transform>

    @Wire
    lateinit var camera: OrthographicCamera
    @Wire
    private lateinit var batch: SpriteBatch

    private lateinit var mapRenderer: OrthogonalTiledMapRenderer


    override fun initialize() {
        super.initialize()
        mapRenderer = OrthogonalTiledMapRenderer(null, 1 / 32f, batch)
    }

    override fun begin() {
        camera.update()
    }

    override fun process(entityId: Int) {
        val transform = transformMapper[entityId]
        mapRenderer.setView(camera.combined, transform.position.x, transform.position.y + 10, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        mapRenderer.map = mapMapper[entityId].map
        mapRenderer.render()
    }
}
