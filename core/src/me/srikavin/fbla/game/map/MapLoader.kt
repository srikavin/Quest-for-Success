package me.srikavin.fbla.game.map

import com.artemis.Aspect
import com.artemis.World
import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.EllipseMapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.EarClippingTriangulator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ktx.assets.disposeSafely
import ktx.log.error
import ktx.log.info
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.ecs.component.*
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.ecs.system.CameraFollowSystem
import me.srikavin.fbla.game.graphics.SpritesheetLoader
import me.srikavin.fbla.game.trigger.TriggerType


private const val COLLISION_LAYER_NAME = "Collision"
private const val TRIGGER_LAYER_NAME = "Trigger"
private const val MAP_SCALE_FACTOR = 1 / 32f

/**
 * Responsible for loading maps and their associated assets as well as spawning players. Tiled Maps that are to be loaded
 * may contain any of the following layers:
 *  * Foreground
 *  * Background
 *  * Collision - Contains all collision boxes on the map
 *  * Trigger - Contains all map triggers and their associated properties; all objects in this layer must have the property
 *  `type` with a value defined in [TriggerType]. All triggers must be [RectangleMapObject]s.
 */
class MapLoader {
    /**
     * Recycled [Vector2] to improve performance and avoid allocating unnecessary objects
     */
    private val recycledVector2 = Vector2()
    private val spritesheetLoader = SpritesheetLoader()
    private val recycledFloatArray = FloatArray(6)
    private val playerAnimations = spritesheetLoader.loadAsespriteSheet("assets/graphics/characters/David.png",
            "assets/graphics/characters/David.json")
    private val coinSprite: TextureRegion = TextureRegion(Texture(Gdx.files.internal("assets/graphics/entity/coinGold.png")))


    /**
     * Creates a player entity with the given world and given position
     */
    private fun createPlayer(world: World, pos: Vector2) {
        world.getSystem(CameraFollowSystem::class.java).camera.position.y = pos.y + 5

        val e = world.createEntity().edit()
                .add(PhysicsBody().apply {
                    shape = PolygonShape().apply {
                        setAsBox(.6f, 1f)
                    }
                    restitution = 0.1f
                    density = 1f
                    friction = 0.1f
                })
                .add(PlayerControlled())
                .add(SpriteOffset(Vector2(-.75f, -1f)))
                .add(Transform().apply { position = pos })
                .add(SwitchableAnimation().apply { animations = playerAnimations; currentState = "Stand" })
                .add(FixedRotation())
                .entity

        world.getSystem(TagManager::class.java).register("PLAYER", e)
    }

    /**
     * Creates a coin entity with the given world and given position
     */
    private fun createCoin(world: World, pos: Vector2) {
        world.createEntity().edit()
                .add(Transform().apply { position = pos })
                .add(Sprite().apply { sprite = coinSprite })
                .add(SpriteScale(0.5f))
                .add(SpriteOffset(Vector2(-0.8f, -0.8f)))
                .add(MapTrigger().apply { type = TriggerType.COIN })
                .add(PhysicsBody().apply {
                    shape = CircleShape().apply {
                        this.radius = .5f
                    }
                    type = BodyDef.BodyType.StaticBody
                })

    }

    /**
     * Loads a map into the given world from the file specified. *All previous entities in the map will be removed.* A
     * player entity will be spawned where a trigger of type `spawn` is found.
     *
     * @param world The world to create entities in
     * @param path The path to load maps from
     *
     * @return The id of the created [MapComponent]
     */
    fun loadMap(world: World, path: String): EntityInt {
        // Unload previously loaded maps
        val entities = world.aspectSubscriptionManager[Aspect.all()].entities
        val loadedMapMapper = world.getMapper(MapComponent::class.java)

        for (i in 0 until entities.size()) {
            if (loadedMapMapper.has(entities[i])) {
                loadedMapMapper[entities[i]].map.disposeSafely()
            }
            world.delete(entities[i])
        }

        val map = TmxMapLoader().load(path, TmxMapLoader.Parameters().apply {
            generateMipMaps = true
            textureMagFilter = Texture.TextureFilter.MipMapNearestNearest
            textureMinFilter = Texture.TextureFilter.MipMapNearestNearest
        })


        val mapEntity: EntityInt = world.create()
        val editor = world.edit(mapEntity)

        editor.add(Transform()).add(MapComponent().apply { this.map = map; this.scaleFactor = MAP_SCALE_FACTOR })

        val triggerLayer: MapLayer? = map.layers.get(TRIGGER_LAYER_NAME)

        if (triggerLayer == null) {
            error { "Layer `$COLLISION_LAYER_NAME` does not exist on map loaded from `$path`" }
            return mapEntity
        }

        val playerPosition = Vector2(5f, 5f)
        var spawnTriggerFound = false

        for (mapObject: MapObject in triggerLayer.objects) {
            if (mapObject is RectangleMapObject) {
                info { "Processing Trigger at ${mapObject.rectangle.x},${mapObject.rectangle.y} in $path" }
                when (mapObject.properties?.get("type")) {
                    "spawn" -> {
                        playerPosition.x = mapObject.rectangle.x
                        playerPosition.y = mapObject.rectangle.y
                        spawnTriggerFound = true
                    }
                    "coin" -> {
                        val pos = Vector2()
                        createCoin(world, mapObject.rectangle.getPosition(pos).scl(MAP_SCALE_FACTOR))
                        info { "Making coin at $pos" }
                    }
                    else -> {
                        val rect = mapObject.rectangle

                        world.createEntity().edit()
                                .add(Transform())
                                .add(PhysicsBody().apply {
                                    shape = PolygonShape().apply {
                                        setAsBox(rect.width * MAP_SCALE_FACTOR * .5f, rect.height * MAP_SCALE_FACTOR * .5f,
                                                rect.getCenter(recycledVector2).scl(MAP_SCALE_FACTOR), 0f)
                                    }
                                    type = BodyDef.BodyType.StaticBody
                                })
                                .add(MapTrigger().apply {
                                    type = TriggerType.valueOf(mapObject.properties.get("type").toString().toUpperCase())
                                    properties = mapObject.properties
                                })
                    }
                }
            } else {
                error { throw RuntimeException("Spawn is of type ${mapObject.javaClass.name} instead of RectangleMapObject in $path") }
            }
        }

        createPlayer(world, playerPosition.scl(MAP_SCALE_FACTOR))

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

                    if (vertices.size / 2 >= 8) {
                        info { "Polygon has greater than 8 vertices [${vertices.size / 2}] and will be triangulated" }
                        val triangulator = EarClippingTriangulator()
                        val triangles = triangulator.computeTriangles(vertices)

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