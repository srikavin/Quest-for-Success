package me.srikavin.fbla.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.TagManager
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.ExtendViewport
import me.srikavin.fbla.game.dialogue.callable.DialogueMeeting
import me.srikavin.fbla.game.ecs.component.DialogueComponent
import me.srikavin.fbla.game.ecs.system.*
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.physics.ContactListenerManager


class FBLAGame : ApplicationAdapter() {
    lateinit var camera: OrthographicCamera
    lateinit var world: World

    override fun create() {
        val cameraScale = 45f
        camera = OrthographicCamera(cameraScale, cameraScale * (9f / 16f))
        camera.position.x = 0f
        camera.position.y = cameraScale * (9f / 16f) * 0.75f

        camera.zoom = 1f
        camera.update()

        val assetManager: AssetManager = AssetManager()
        val batch = SpriteBatch()


        val physicsWorld = com.badlogic.gdx.physics.box2d.World(Vector2(0f, -16f), true)


        val generator = FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Kenney Pixel.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 48
        val font12: BitmapFont = generator.generateFont(parameter)

        val fontMap = ObjectMap<String, Any>()
        fontMap.put("KenneyPixel", font12)
        generator.dispose()

        assetManager.load("assets/skin/skin.json", Skin::class.java, SkinLoader.SkinParameter(fontMap))
        assetManager.finishLoading()
        val skin: Skin = assetManager.get<Skin>("assets/skin/skin.json")

        val stage = Stage(ExtendViewport(640f, 480f))
        val root = Table(skin)
        stage.addActor(root)

        root.setFillParent(true)
        root.top().right()
        root.debug = true

        val listenerManager = ContactListenerManager()

        val config = WorldConfigurationBuilder()
                .with(InputSystem(listenerManager),
                        PhysicsSystem(physicsWorld, listenerManager),
                        TriggerSystem(listenerManager),
                        CameraFollowSystem(),
                        PlayerAnimationSystem(),
                        RenderSystem(),
                        BackgroundRenderSystem(),
                        EntityRenderSystem(),
                        MinigameRenderSystem(),
                        DialogueSystem(),
                        UISystem(),
                        PhysicsDebugSystem(physicsWorld)
                )
                .with(TagManager())
                .build()
                .register(physicsWorld)
                .register(camera)
                .register(batch)
                .register(skin)
                .register(stage)
                .register(root)

        world = World(config)

        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))

        val mapLoader = MapLoader(assetManager, world)
        mapLoader.loadMap("assets/maps/level1.tmx")

        world.createEntity().edit().add(DialogueComponent().apply { script = DialogueMeeting() })

    }

    override fun render() {
        world.setDelta(Gdx.graphics.deltaTime)
        world.process()
    }

    override fun dispose() {
    }
}
