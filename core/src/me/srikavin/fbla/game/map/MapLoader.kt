package me.srikavin.fbla.game.map

import com.artemis.World
import com.artemis.managers.TagManager
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
import ktx.log.error
import ktx.log.info
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.ecs.component.*
import me.srikavin.fbla.game.ecs.component.Map
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.ecs.system.CameraFollowSystem
import me.srikavin.fbla.game.graphics.SpritesheetLoader


private const val COLLISION_LAYER_NAME = "Collision"
private const val TRIGGER_LAYER_NAME = "Trigger"
private const val MAP_SCALE_FACTOR = 1 / 32f

class MapLoader(private val assetManager: AssetManager, private val world: World) {
    private val recycledVector2 = Vector2()
    private val spritesheetLoader = SpritesheetLoader()
    private val recycledFloatArray = FloatArray(6)

    private fun createPlayer(pos: Vector2) {
        val playerAnimations = spritesheetLoader.loadAsespriteSheet("David.png", "David.json")

        world.getSystem(CameraFollowSystem::class.java).camera.position.y = pos.y + 5

        val e = world.createEntity().edit()
                .add(PhysicsBody().apply {
                    shape = PolygonShape().apply {
                        setAsBox(.6f, 1f)
                    }
                    restitution = 0f
                    density = 1f
                    friction = 0.2f
                })
                .add(PlayerControlled())
                .add(SpriteOffset(Vector2(-.75f, -1f)))
                .add(Transform().apply { position = pos })
                .add(SwitchableAnimation().apply { animations = playerAnimations; currentState = "Stand" })
                .add(FixedRotation())
                .entity

        world.getSystem(TagManager::class.java).register("PLAYER", e)
    }

    fun loadMap(path: String): EntityInt {
        val map = when {
            assetManager.isLoaded(path) -> assetManager.get<TiledMap>(path)
            else -> {
                assetManager.load(path, TiledMap::class.java)
                assetManager.finishLoadingAsset<TiledMap>(path)
            }
        }

//        val entities = world.aspectSubscriptionManager[Aspect.all()].entities

//        for (i in 0 until entities.size()) {
//            info { i.toString() }
//            world.delete(entities[i])
//        }
//        world.entityManager.reset()

        val mapEntity: EntityInt = world.create()
        val editor = world.edit(mapEntity)

        editor.add(Transform()).add(Map().apply { this.map = map; this.scaleFactor = MAP_SCALE_FACTOR })

        val triggerLayer: MapLayer? = map.layers.get(TRIGGER_LAYER_NAME)

        if (triggerLayer == null) {
            error { "Layer `$COLLISION_LAYER_NAME` does not exist on map loaded from `$path`" }
            return mapEntity
        }

        val playerPosition = Vector2(5f, 5f)
        var spawnTriggerFound = false

        for (mapObject: MapObject in triggerLayer.objects) {
            when (mapObject.properties?.get("type")) {
                "spawn" -> {
                    if (mapObject is RectangleMapObject) {
                        playerPosition.x = mapObject.rectangle.x
                        playerPosition.y = mapObject.rectangle.y
                        spawnTriggerFound = true
                    } else {
                        error { throw RuntimeException("Spawn is of type ${mapObject.javaClass.name} instead of RectangleMapObject in $path") }
                    }
                }
                "coin" -> {
                    //TODO: Spawn Coins
                }
                "transition" -> {
                    //TODO: Transition Level
                }
            }
        }

        createPlayer(playerPosition.scl(MAP_SCALE_FACTOR))

        if (!spawnTriggerFound) {
            error { throw RuntimeException("No Spawn trigger found in $path") }
        }


        val collisionLayer: MapLayer? = map.layers.get(COLLISION_LAYER_NAME)

        if (collisionLayer == null) {
            info { "Layer `$COLLISION_LAYER_NAME` does not exist on map loaded from `$path`" }
            return mapEntity
        }

        val fixtureDefs = GdxArray<FixtureDef>(false, collisionLayer.objects.count)

        loop@ for (mapObject: MapObject in collisionLayer.objects) {
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
        info { collisionLayer.objects.count.toString() }
        editor.add(PhysicsBody(fixtureDefs, BodyDef.BodyType.StaticBody, 0f, 0.2f, 0f))
        editor.entity

        return mapEntity
    }
}