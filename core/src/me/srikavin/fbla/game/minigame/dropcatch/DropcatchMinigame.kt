package me.srikavin.fbla.game.minigame.dropcatch

import com.artemis.managers.TagManager
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import ktx.log.info
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.GameState
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Sprite
import me.srikavin.fbla.game.ecs.component.SpriteOffset
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.graphics.MAP_SCALE_FACTOR
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.minigame.Minigame
import me.srikavin.fbla.game.physics.ContactListenerManager

class DropcatchMinigame : Minigame() {
    private val inputs = GdxArray<Int>()
    private lateinit var itemsLeft: TypingLabel
    private lateinit var timeLeft: TypingLabel
    private var goodItemsLeft = 0
    private var timeLeftVal = 60f
    private lateinit var gameState: GameState

    init {
        inputs.add(Input.Keys.NUM_1)
        inputs.add(Input.Keys.NUM_2)
        inputs.add(Input.Keys.NUM_3)
        inputs.add(Input.Keys.NUM_4)
        inputs.add(Input.Keys.NUM_5)
    }

    override fun resetMinigame(properties: MapProperties) {

    }

    override fun initializeMinigame(skin: Skin, stage: Stage) {
        val table = Table(skin).center().bottom()
        table.setFillParent(true)

        stage.addActor(table)
        itemsLeft = TypingLabel("Items Left: ?", skin)
        timeLeft = TypingLabel("Time Left: ?", skin)
        table.add(itemsLeft)
        table.row()
        table.add(timeLeft)

        gameState = world.getRegistered(GameState::class.java)

        val mapper = world.getMapper(DropcatchItemComponent::class.java)
        val contactListenerManager = world.getRegistered(ContactListenerManager::class.java)
        val physicsWorld = world.getRegistered(World::class.java)


        contactListenerManager.addListener(object : ContactListener {
            override fun endContact(contact: Contact?) {

            }

            override fun beginContact(contact: Contact) {
                val playerId = world.getSystem(TagManager::class.java).getEntityId("PLAYER")
                val other: Fixture = when {
                    contact.fixtureA.userData == playerId -> {
                        contact.fixtureB
                    }
                    contact.fixtureB.userData == playerId -> {
                        contact.fixtureA
                    }
                    else -> {
                        // Does not involve player
                        return
                    }
                }

                val otherId = other.userData as EntityInt
                info { otherId.toString() }
                if (mapper.has(otherId)) {
                    val comp = mapper.get(otherId)
                    if (comp.type == DropcatchItemType.GOOD) {
                        gameState.score += 1
                        goodItemsLeft -= 1
                    } else {
                        gameState.score -= 1
                    }
                    Gdx.app.postRunnable {
                        world.delete(otherId)
                    }
                }
            }

            override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
            }

            override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
            }
        })

        val cache: ObjectMap<String, TextureRegion> = ObjectMap(2)

        mapLoader.loadMap(world, "assets/maps/dropcatch_${this.mapProperties.subtype}.tmx",
                MapLoader.UnloadType.NonMinigame) { mapObject: RectangleMapObject, type: String, path: String ->
            val triggerProperties = MapTriggerProperties(mapObject.properties)

            if (!cache.containsKey(triggerProperties.subtype)) {
                cache.put(triggerProperties.subtype, TextureRegion(Texture(Gdx.files.internal(triggerProperties.subtype))))
            }

            val sprite = cache.get(triggerProperties.subtype)

            val editor = world.createEntity().edit()
                    .add(Transform().apply { position = mapObject.rectangle.getPosition(Vector2()).scl(MAP_SCALE_FACTOR) })
                    .add(PhysicsBody().apply {
                        this.shape = PolygonShape().apply {
                            this.setAsBox(.5f, .5f)
                        }
                        this.type = BodyDef.BodyType.StaticBody
                        this.isSensor = true
                    })
                    .add(Sprite().apply { this.sprite = sprite })
                    .add(SpriteOffset(Vector2(-.5f, -.5f)))

            when (type) {
                "gooditem" -> {
                    editor.add(DropcatchItemComponent().apply { this.type = DropcatchItemType.GOOD })
                    goodItemsLeft += 1
                }
                "baditem" -> {
                    editor.add(DropcatchItemComponent().apply { this.type = DropcatchItemType.BAD })
                }
                else -> {
                    error("Unknown dropcatch minigame type: $type")
                }
            }
        }
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage) {
        itemsLeft.setText("Items Left: $goodItemsLeft")
        timeLeft.setText("Time Left: ${"%.1f".format(timeLeftVal)}")
    }

    override fun shouldRenderBackground(): Boolean {
        return true
    }

    override fun allowPlayerMovement(): Boolean {
        return true
    }

    override fun process(delta: Float) {
        timeLeftVal -= delta

        if (timeLeftVal <= 0 || goodItemsLeft == 0) {
            Gdx.app.postRunnable {
                this.endMinigame()
            }
        }
    }

}