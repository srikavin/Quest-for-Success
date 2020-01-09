package me.srikavin.fbla.game.minigame.dropcatch

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Sprite
import me.srikavin.fbla.game.ecs.component.SpriteOffset
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.graphics.MAP_SCALE_FACTOR
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.minigame.Minigame

class DropcatchMinigame : Minigame() {
    private val inputs = GdxArray<Int>()
    private lateinit var itemsLeft: TypingLabel
    private lateinit var timeLeft: TypingLabel
    private var goodItemsLeft = 0
    private var timeLeftVal = 60f

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

        table.debug = true
        stage.addActor(table)
        itemsLeft = TypingLabel("Items Left: ?", skin)
        timeLeft = TypingLabel("Time Left: ?", skin)
        table.add(itemsLeft)
        table.row()
        table.add(timeLeft)

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

            val mapper = world.getMapper(DropcatchItemComponent::class.java)
//            val contactListenerManager = world.get

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
    }

}