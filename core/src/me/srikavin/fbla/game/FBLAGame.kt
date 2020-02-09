package me.srikavin.fbla.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.TagManager
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.strongjoshua.console.CommandExecutor
import com.strongjoshua.console.GUIConsole
import ktx.assets.disposeSafely
import me.srikavin.fbla.game.Scene.PLAYING
import me.srikavin.fbla.game.Scene.TITLE
import me.srikavin.fbla.game.ecs.component.MinigameComponent
import me.srikavin.fbla.game.ecs.system.*
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.minigame.dropcatch.DropcatchMinigame
import me.srikavin.fbla.game.physics.ContactListenerManager
import me.srikavin.fbla.game.ui.MainMenu

const val cameraScale = 45f

enum class Scene {
    PLAYING,
    TITLE,
    LOADING
}

class FBLAGame : ApplicationAdapter() {
    private lateinit var camera: OrthographicCamera
    private lateinit var world: World
    private lateinit var batch: SpriteBatch
    private lateinit var skin: Skin
    private var assetManager = AssetManager()
    private lateinit var splashImage: Texture
    private lateinit var console: GUIConsole

    private lateinit var mainMenuUI: MainMenu

    private var scene: Scene = Scene.LOADING

    override fun resize(width: Int, height: Int) {
        if (scene == PLAYING) {
            camera.viewportHeight = cameraScale * (height.toFloat() / width)
            camera.viewportWidth = cameraScale

            camera.zoom = 1f
            camera.update()
        } else if (scene == TITLE) {
            mainMenuUI.build()
        }
    }

    private fun startGame() {
        camera = OrthographicCamera(cameraScale, cameraScale * (9f / 16f))
        camera.position.x = 0f
        camera.position.y = cameraScale * (9f / 16f) * 0.75f

        val physicsWorld = com.badlogic.gdx.physics.box2d.World(Vector2(0f, -20f), true)

        val stage = Stage(ExtendViewport(1920f, 1080f))
        val root = Table(skin)
        stage.addActor(root)

        root.setFillParent(true)
        root.top().right()

        val gameState = GameState(0)

        val listenerManager = ContactListenerManager()

        val mapLoader = MapLoader()

        val config = WorldConfigurationBuilder()
                .with(InputSystem(listenerManager),
                        PhysicsSystem(physicsWorld, listenerManager),
                        CameraFollowSystem(),
                        PlayerAnimationSystem(),
                        RenderSystem(),
                        BackgroundRenderSystem(),
                        EntityRenderSystem(),
                        MinigameSystem(),
                        MinigameRenderSystem(),
                        DialogueSystem(),
                        TriggerSystem(listenerManager),
                        UISystem(),
                        PhysicsDebugSystem(physicsWorld, debug = true)
                )
                .with(TagManager())
                .build()
                .register(physicsWorld)
                .register(camera)
                .register(batch)
                .register(skin)
                .register(stage)
                .register(root)
                .register(gameState)
                .register(mapLoader)
                .register(listenerManager)

        world = World(config)
        mapLoader.loadMap(world, "assets/maps/level1.tmx")

        console = GUIConsole()

        @Suppress("unused")
        console.setCommandExecutor(object : CommandExecutor() {
            fun loadLevel(name: String) {
                mapLoader.loadMap(world, "assets/maps/${name}.tmx")
            }

            fun mainMenu() {
                afterLoad()
            }

            fun dropcatch(stage: String) {
                world.createEntity().edit().add(MinigameComponent().apply {
                    minigame = DropcatchMinigame().apply {
                        reset(MapProperties().apply { put("subtype", stage); put("next_level", "level1.tmx") }, world, mapLoader)
                    }
                })

            }

            fun debug(boolean: Boolean) {
                world.getSystem(PhysicsDebugSystem::class.java).debug = boolean
            }
        })
        console.displayKeyID = Input.Keys.ALT_RIGHT

    }

    override fun create() {
        Colors.put("accent", Color.valueOf("#AA3E39"))
        Colors.put("green", Color.valueOf("#00FF00"))

        batch = SpriteBatch()
        splashImage = Texture(Gdx.files.internal("assets/graphics/titlelogo.png"))

        // Load necessary resources asynchronously
        val resolver: FileHandleResolver = InternalFileHandleResolver()
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(resolver))
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))


        fun newCursor(name: String): Cursor {
            val p = Pixmap(Gdx.files.internal("assets/cursors/$name.png"))
            return Gdx.graphics.newCursor(p, 32, 32)
        }

        Gdx.graphics.setCursor(newCursor("cursor"))

        val parameter = FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
            fontParameters.apply {
                size = 22
                fontFileName = "assets/fonts/font.ttf"
                gamma = .8f
            }
        }
        assetManager.load("defaultFont.ttf", BitmapFont::class.java, parameter)
        val font = assetManager.finishLoadingAsset<BitmapFont>("defaultFont.ttf")
        font.data.apply {
            markupEnabled = true
        }

        val fontMap = ObjectMap<String, Any>()
        fontMap.put("defaultFont", font)

        assetManager.load("assets/skin/skin.json", Skin::class.java, SkinLoader.SkinParameter(fontMap))
    }

    fun afterLoad() {
        skin = assetManager.get("assets/skin/skin.json")
        mainMenuUI = MainMenu(skin) {
            startGame()
            mainMenuUI.disposeSafely()
            scene = PLAYING
        }
        mainMenuUI.build()
        scene = TITLE
    }

    override fun render() {
        when (scene) {
            PLAYING -> {
                world.setDelta(Gdx.graphics.deltaTime)
                world.process()
                console.draw()
            }
            TITLE -> {
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

                mainMenuUI.render()
            }
            Scene.LOADING -> {
                // Render splash image
                Gdx.gl.glClearColor(20f, 20f, 20f, 1f)
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
                batch.begin()
                batch.draw(splashImage, (Gdx.graphics.width / 2f) - 128f, (Gdx.graphics.height / 2f) - 182f,
                        256f, 264f)
                batch.end()
                // Continue loading resources
                if (assetManager.update()) {
                    afterLoad()
                }
            }
        }
    }

    override fun dispose() {
        assetManager.disposeSafely()
        world.dispose()
    }
}
