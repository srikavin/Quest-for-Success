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
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.box2d.earthGravity
import me.srikavin.fbla.game.dialogue.callable.DialogueMeeting
import me.srikavin.fbla.game.ecs.component.*
import me.srikavin.fbla.game.ecs.system.*
import me.srikavin.fbla.game.graphics.SpritesheetLoader
import me.srikavin.fbla.game.map.MapLoader


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
        val spritesheetLoader = SpritesheetLoader()

        val playerAnimations = spritesheetLoader.loadAsespriteSheet("David.png", "David.json")

        val physicsWorld = com.badlogic.gdx.physics.box2d.World(earthGravity, true)


        val generator = FreeTypeFontGenerator(Gdx.files.internal("fonts/Kenney Pixel.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 48
        val font12: BitmapFont = generator.generateFont(parameter) // font size 12 pixels

        val fontMap = ObjectMap<String, Any>()
        fontMap.put("KenneyPixel", font12)
        generator.dispose() // don't forget to dispose to avoid memory leaks!

        assetManager.load("skin/skin.json", Skin::class.java, SkinLoader.SkinParameter(fontMap))
        assetManager.finishLoading()
        val skin: Skin = assetManager.get<Skin>("skin/skin.json")

        val stage = Stage(ExtendViewport(640f, 480f))
        val root = Table(skin)
        stage.addActor(root)

        root.setFillParent(true)
        root.top().right()
        root.debug = true


        val config = WorldConfigurationBuilder()
                .with(InputSystem(),
                        PhysicsSystem(physicsWorld),
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
                .register(camera)
                .register(batch)
                .register(skin)
                .register(stage)
                .register(root)

        world = World(config)

        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))

        val mapLoader = MapLoader(assetManager, world)
        mapLoader.loadMap("untitled.tmx")

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
                .add(Transform().apply { position = Vector2(3f, 15f) })
                .add(SwitchableAnimation().apply { animations = playerAnimations; currentState = "Stand" })
                .add(FixedRotation())
                .entity

        world.createEntity().edit().add(DialogueComponent().apply { script = DialogueMeeting() })

        world.getSystem(TagManager::class.java).register("PLAYER", e)
    }

    override fun render() {
        world.setDelta(Gdx.graphics.deltaTime)
        world.process()
    }

    override fun dispose() {
    }
}
