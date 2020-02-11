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
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ObjectMap
import ktx.log.info
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Sprite
import me.srikavin.fbla.game.ecs.component.SpriteOffset
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.ext.sequence
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.graphics.MAP_SCALE_FACTOR
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.minigame.Minigame
import me.srikavin.fbla.game.physics.ContactListenerManager
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.EntityInt
import me.srikavin.fbla.game.util.GdxArray

const val style_name = "menu"

class DropcatchMinigame : Minigame() {
    private val inputs = GdxArray<Int>()
    private lateinit var itemsLeft: Label
    private lateinit var timeLeft: Label
    private lateinit var gameState: GameState
    private lateinit var table: Table
    private lateinit var infoPanel: Table
    private lateinit var container: Table
    private lateinit var contactListener: ContactListener

    private var firstRender = true
    private var haveShownScoreboard = false
    private var goodItemsLeft = 0
    private var timeLeftVal = 60f

    private val goodItems = ArrayList<Drawable>()
    private val badItems = ArrayList<Drawable>()

    private var goodCollected = 0
    private var badCollected = 0


    init {
        inputs.add(Input.Keys.NUM_1)
        inputs.add(Input.Keys.NUM_2)
        inputs.add(Input.Keys.NUM_3)
        inputs.add(Input.Keys.NUM_4)
        inputs.add(Input.Keys.NUM_5)
    }

    override fun resetMinigame(properties: MapProperties) {
        goodItems.clear()
        badItems.clear()
        timeLeftVal = 45f
        goodCollected = 0
        goodItemsLeft = 0
        badCollected = 0
        firstRender = true
        haveShownScoreboard = false
    }

    override fun initializeMinigame(skin: Skin, stage: Stage) {
        container = Table(skin)
        container.setSize(1920f, 1080f)
        container.setFillParent(true)

        stage.addActor(container)

        table = Table(skin).center().bottom()
        table.setSize(170f, 50f)
        table.background(NinePatchDrawable(skin.getPatch("menu-button-bg")))

        container.addActor(table)

        itemsLeft = Label("Items Left: ?", skin, style_name)
        timeLeft = Label("Time Left: ?", skin, style_name)

        table.add(itemsLeft)
        table.row()
        table.add(timeLeft)


        gameState = world.getRegistered(GameState::class.java)

        val mapper = world.getMapper(DropcatchItemComponent::class.java)

        val contactListenerManager = world.getRegistered(ContactListenerManager::class.java)

        contactListener = object : ContactListener {
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
                        goodCollected++
                    } else {
                        gameState.score -= 1
                        badCollected++
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
        }
        contactListenerManager.addListener(contactListener)

        val cache: ObjectMap<String, TextureRegion> = ObjectMap(2)

        mapLoader.loadMap(world, "assets/maps/dropcatch_${this.mapProperties.subtype}.tmx",
                MapLoader.UnloadType.NonMinigame) { mapObject: RectangleMapObject, type: String, path: String ->
            val triggerProperties = MapTriggerProperties(mapObject.properties)

            if (!cache.containsKey(triggerProperties.subtype)) {
                val texture = TextureRegion(Texture(Gdx.files.internal(triggerProperties.subtype)))
                cache.put(triggerProperties.subtype, texture)

                if (type == "gooditem") {
                    goodItems.add(TextureRegionDrawable(texture))
                } else if (type == "baditem") {
                    badItems.add(TextureRegionDrawable(texture))
                }
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
                    goodItemsLeft++
                }
                "baditem" -> {
                    editor.add(DropcatchItemComponent().apply { this.type = DropcatchItemType.BAD })
                }
                else -> {
                    error("Unknown dropcatch minigame type: $type")
                }
            }
        }

        infoPanel = container.table(NinePatchDrawable(table.skin.getPatch("menu-button-bg"))) {
            it.add("Collect the items as fast as possible, while avoiding unwanted objects!", style_name)
            it.row()
            it.add().height(15f)
            it.row()
            it.table { t ->
                t.table { inner ->
                    inner.add("[green]Collect:[]", style_name).actor.setScale(3f)

                    for (item in goodItems) {
                        info { "$goodItems" }
                        inner.row()
                        inner.add(Image(item)).height(50f).width(50f * item.minWidth / item.minHeight)
                    }
                }

                t.add().width(40f)

                t.table { inner ->
                    inner.add("[accent]Avoid:[]", style_name).actor.setScale(3f)

                    for (item in badItems) {
                        info { "$badItems" }
                        inner.row()
                        inner.add(Image(item)).height(50f).width(50f * item.minWidth / item.minHeight)
                    }
                }
            }.center().bottom()
        }.width(880f).height(150f).fill().actor

    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage) {
        if (firstRender) {
            table.isTransform = true
            container.isTransform = true
            table.clearActions()
            table.sequence(
                    Actions.moveTo(1920 / 2f - 150f, 1080 / 4f),
                    Actions.scaleTo(3f, 3f),
                    Actions.parallel(
                            Actions.moveTo(1920 / 2f - 80f, table.y, 2.5f, Interpolation.pow2In),
                            Actions.scaleTo(1f, 1f, 2.5f, Interpolation.pow2In)
                    )
            )

            infoPanel.clearActions()
            infoPanel.sequence(
                    Actions.fadeIn(7f),
                    Actions.fadeOut(3f),
                    Actions.hide()
            )

            firstRender = false
        }

        itemsLeft.setText("Items Left: $goodItemsLeft")
        timeLeft.setText("Time Left: ${timeLeftVal.toInt()}")
    }

    override fun shouldRenderBackground(): Boolean {
        return true
    }

    override fun allowPlayerMovement(): Boolean {
        return !(timeLeftVal <= 0 || goodItemsLeft == 0)
    }

    override fun process(delta: Float) {
        if (timeLeftVal <= 0 || goodItemsLeft == 0) {
            if (!haveShownScoreboard) {
                infoPanel.clear()

                infoPanel.let {
                    val response = when {
                        goodItemsLeft == 0 -> "Perfect! You got all of the items!"
                        goodItemsLeft < 5 -> "You nearly got all of them!"
                        goodItemsLeft < 10 -> "You could've gotten more."
                        else -> "Good effort! Better luck next time."
                    }
                    it.add(response, style_name)
                    it.row()
                    it.add("You collected [green]$goodCollected items[], but also [accent]$badCollected unwanted objects[]!", style_name)
                    it.row()
                    it.add("Score: [green]${goodCollected - badCollected}[]", style_name)
                }

                infoPanel.sequence(
                        Actions.show(),
                        Actions.fadeIn(3f),
                        Actions.fadeIn(5f),
                        Actions.fadeOut(2f),
                        Actions.hide(),
                        Actions.run {
                            val contactListenerManager = world.getRegistered(ContactListenerManager::class.java)
                            contactListenerManager.removeListener(contactListener)
                            Gdx.app.postRunnable {
                                this.endMinigame()
                            }
                        }
                )
                haveShownScoreboard = true
            }
        } else {
            timeLeftVal -= delta
        }
    }

}