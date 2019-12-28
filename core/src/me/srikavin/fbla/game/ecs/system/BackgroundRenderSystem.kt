package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
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

    private val bgCamera = OrthographicCamera()

    override fun initialize() {
        super.initialize()
        mapRenderer = OrthogonalTiledMapRenderer(null, 1 / 32f, batch)
    }

    override fun begin() {
        bgCamera.position.set(camera.position).scl(0.40f, 1f, 1f).add(30f, 0f, 0f)
        bgCamera.direction.set(camera.direction)
        bgCamera.zoom = camera.zoom * 1.25f
        bgCamera.viewportHeight = camera.viewportHeight
        bgCamera.viewportWidth = camera.viewportWidth
        bgCamera.update()
        camera.update()
    }

    private val layers: ArrayList<Int> = ArrayList()
    private lateinit var layersArr: IntArray
    private lateinit var map: TiledMap
    private lateinit var backgroundLayer: TiledMapTileLayer

    override fun process(entityId: Int) {
        val transform = transformMapper[entityId]
        val map = mapMapper[entityId].map

        if (!this::map.isInitialized || this.map != map) {
            this.map = map
            layers.clear()

            map.layers.forEachIndexed { index, mapLayer ->
                if (mapLayer.name == "Background") {
                    backgroundLayer = mapLayer as TiledMapTileLayer
                } else {
                    layers.add(index)
                }
            }

            layersArr = layers.toIntArray()
            mapRenderer.map = map
        }


        batch.begin()
        mapRenderer.setView(bgCamera.combined, transform.position.x, transform.position.y, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        mapRenderer.renderTileLayer(backgroundLayer)
        batch.end()

        mapRenderer.setView(camera.combined, transform.position.x, transform.position.y, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        mapRenderer.render(layersArr)
    }
}
