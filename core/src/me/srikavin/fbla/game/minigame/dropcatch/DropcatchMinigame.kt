package me.srikavin.fbla.game.minigame.dropcatch

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.rafaskoberg.gdx.typinglabel.TypingLabel
import me.srikavin.fbla.game.GdxArray
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.minigame.Minigame

class DropcatchMinigame : Minigame() {
    private val inputs = GdxArray<Int>()

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
        stage.root = Table(skin)
        stage.root.addActor(TypingLabel("Text", skin))
        val recycled = Vector2()

        mapLoader.loadMap(world, "assets/maps/dropcatch_${this.mapProperties.subtype}.tmx",
                MapLoader.UnloadType.NonMinigame) { mapObject: RectangleMapObject, type: String, path: String ->
            when (type) {
                "gooditem" -> {
                    world.createEntity().edit()
                            .add(DropcatchItemComponent().apply { this.type = DropcatchItemType.GOOD })
                            .add(Transform().apply { position = mapObject.rectangle.getPosition(recycled) })
//                            .add(PhysicsBody().apply { this. })
                }
                "baditem" -> {

                }
                else -> {
                    error("Unknown dropcatch minigame type: $type")
                }
            }
        }
    }

    override fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage) {

    }

    override fun shouldRenderBackground(): Boolean {
        return true
    }

    override fun allowPlayerMovement(): Boolean {
        return true
    }

    override fun process(delta: Float) {

    }

}