package me.srikavin.fbla.game.map

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.utils.XmlReader
import ktx.log.debug
import ktx.log.error
import ktx.log.info
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.ecs.component.Map
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Transform

private const val COLLISION_LAYER_NAME = "Collision"
private const val MAP_SCALE_FACTOR = 1 / 32f

class MapLoader(private val assetManager: AssetManager, private val world: World) {
    private val recycledVector2 = Vector2()
    private val xmlReader = XmlReader()
    private val recycledFloatArray = FloatArray(6)

    fun loadMap(path: String): EntityInt {
        val map = when {
            assetManager.isLoaded(path) -> assetManager.get<TiledMap>(path)
            else -> {
                assetManager.load(path, TiledMap::class.java)
                assetManager.finishLoadingAsset<TiledMap>(path)
            }
        }

        // Manually handle collision + trigger layers
        xmlReader.parse(Gdx.files.internal(path))

        val mapEntity: EntityInt = world.create()
        val editor = world.edit(mapEntity)

        editor.add(Transform()).add(Map().apply { this.map = map; this.scaleFactor = MAP_SCALE_FACTOR });

        val layer: MapLayer? = map.layers.get(COLLISION_LAYER_NAME)

        if (layer == null) {
            info { "Layer `$COLLISION_LAYER_NAME` does not exist on map loaded from `$path`" }
            return mapEntity
        }

        val fixtureDefs = GdxArray<FixtureDef>(false, layer.objects.count)

        loop@ for (mapObject: MapObject in layer.objects) {
            info { mapObject.toString() }
            val shape: Shape = when (mapObject) {
                is PolygonMapObject -> {
                    val vertices = mapObject.polygon.transformedVertices

                    if (vertices.size < 6) {
                        error { "Polygon in collision layer has less than 3 [${vertices.size}] vertices!" }
                        continue@loop
                    }

                    for (i in vertices.indices) {
                        vertices[i] *= MAP_SCALE_FACTOR
                    }
                    info { vertices.joinToString { e -> e.toString() } }

                    if (vertices.size > 8) {
                        info { "Polygon has greater than 8 vertices [${vertices.size / 2}] and will be triangulated" }
                        val triangulator = EarClippingTriangulator()
                        val triangles = triangulator.computeTriangles(vertices)

                        info { vertices.joinToString { e -> e.toString() } }
                        info { triangles.toArray().joinToString { e -> e.toString() } }

                        if (triangles.size % 6 != 0) {
                            throw RuntimeException("Invalid triangles returned!")
                        }

                        for (x in 0 until triangles.size step 3) {
                            val fixtureDef = FixtureDef()
                            fixtureDef.shape = PolygonShape().apply {
                                recycledFloatArray[0] = vertices[triangles[x + 0] * 2]
                                recycledFloatArray[1] = vertices[triangles[x + 0] * 2 + 1]
                                recycledFloatArray[2] = vertices[triangles[x + 1] * 2]
                                recycledFloatArray[3] = vertices[triangles[x + 1] * 2 + 1]
                                recycledFloatArray[4] = vertices[triangles[x + 2] * 2]
                                recycledFloatArray[5] = vertices[triangles[x + 2] * 2 + 1]
                                this.set(recycledFloatArray)
                            }
                            fixtureDefs.add(fixtureDef)
                        }

                        continue@loop
                    } else {
                        PolygonShape().apply {
                            set(vertices)
                        }
                    }
                }

                is RectangleMapObject -> {
                    val rect = mapObject.rectangle
                    info { rect.getCenter(recycledVector2).scl(MAP_SCALE_FACTOR).toString() }
                    PolygonShape().apply {
                        setAsBox(rect.width * MAP_SCALE_FACTOR * .5f, rect.height * MAP_SCALE_FACTOR * .5f,
                                rect.getCenter(recycledVector2).scl(MAP_SCALE_FACTOR), 0f)
                    }
                }

                is EllipseMapObject -> {
                    val e = mapObject.ellipse
                    val radius = e.width * 0.5f
                    info { "Changing ellipse map object into a circle: [width: ${e.width}, height: ${e.height}] -> [radius: ${radius}]" }
                    CircleShape().apply {
                        this.radius = radius * MAP_SCALE_FACTOR
                        this.position = recycledVector2.set(e.x + radius, e.y + radius).scl(MAP_SCALE_FACTOR)
                    }
                }
                else -> {
                    throw RuntimeException("Unknown type ${mapObject.javaClass.name}")
                }
            }

            val fixtureDef = FixtureDef().apply {
                this.shape = shape
            }
            fixtureDefs.add(fixtureDef)
        }

        info { fixtureDefs.toString() }
        info { layer.objects.count.toString() }
        editor.add(PhysicsBody(fixtureDefs, BodyDef.BodyType.StaticBody, 0f, 0.2f, 0f))
        editor.entity

        return mapEntity
    }
}