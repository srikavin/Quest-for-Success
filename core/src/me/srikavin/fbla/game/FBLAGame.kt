package me.srikavin.fbla.game

import com.artemis.World
import com.artemis.WorldConfigurationBuilder
import com.artemis.managers.TagManager
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ObjectMap
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.FitViewport
import ktx.actors.onClick
import me.srikavin.fbla.game.Scene.PLAYING
import me.srikavin.fbla.game.Scene.TITLE
import me.srikavin.fbla.game.ecs.system.*
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.physics.ContactListenerManager

const val cameraScale = 45f

enum class Scene {
    PLAYING,
    TITLE
}

class FBLAGame : ApplicationAdapter() {
    lateinit var camera: OrthographicCamera
    lateinit var world: World
    lateinit var batch: SpriteBatch
    lateinit var skin: Skin

    var scene: Scene = TITLE

    override fun resize(width: Int, height: Int) {
        if (scene == PLAYING) {
            camera.viewportHeight = cameraScale * (height.toFloat() / width)
            camera.viewportWidth = cameraScale

            camera.zoom = 1f
            camera.update()
        } else {
            stageBg.viewport.update(width, height)
        }
    }

    private fun startGame() {
        camera = OrthographicCamera(cameraScale, cameraScale * (9f / 16f))
        camera.position.x = 0f
        camera.position.y = cameraScale * (9f / 16f) * 0.75f

        val physicsWorld = com.badlogic.gdx.physics.box2d.World(Vector2(0f, -23f), true)
        batch = SpriteBatch()

        val stage = Stage(ExtendViewport(640f, 480f))
        val root = Table(skin)
        stage.addActor(root)

        root.setFillParent(true)
        root.top().right()
        root.debug = true

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
                .register(gameState)
                .register(mapLoader)
                .register(listenerManager)

        world = World(config)
        mapLoader.loadMap(world, "assets/maps/level2.tmx")
    }

    private lateinit var titleBg: TextureRegion
    private lateinit var stageBg: Stage

    override fun create() {
        val assetManager = AssetManager()
        assetManager.setLoader(TiledMap::class.java, TmxMapLoader(InternalFileHandleResolver()))

        val generator = FreeTypeFontGenerator(Gdx.files.internal("assets/fonts/Kenney Pixel.ttf"))
        val parameter = FreeTypeFontGenerator.FreeTypeFontParameter()
        parameter.size = 48
        val font12: BitmapFont = generator.generateFont(parameter)

        val fontMap = ObjectMap<String, Any>()
        fontMap.put("KenneyPixel", font12)
        generator.dispose()

        assetManager.load("assets/skin/skin.json", Skin::class.java, SkinLoader.SkinParameter(fontMap))
        assetManager.finishLoading()
        skin = assetManager.get<Skin>("assets/skin/skin.json")

        camera = OrthographicCamera(cameraScale, cameraScale * (9f / 16f))
        titleBg = TextureRegion(Texture(Gdx.files.internal("assets/graphics/homescreen.png")))

        stageBg = Stage(FitViewport(640f, 640f))
        val table = Table(skin)
        table.setFillParent(true)

        table.background(TextureRegionDrawable(titleBg))
        stageBg.addActor(table)

        table.bottom()

        val vertGroup = VerticalGroup()
        vertGroup.padBottom(150f)
        table.add(vertGroup)

        vertGroup.space(3f)

        val play = Button(skin, "green").apply {
            add("Play")
            pad(10f)
        }

        val instructions = Button(skin).apply {
            add("Instructions").actor.setFontScale(0.75f)
            pad(5f)
        }

        val exit = Button(skin, "red").apply {
            add("Quit").actor.setFontScale(0.7f)
            pad(10f)
        }

        play.onClick {
            startGame()
            scene = PLAYING
        }

        instructions.onClick {
            val dialog = object : Dialog("Instructions", skin) {
                override fun result(obj: Any?) {
                    this.remove()
                }
            }
            dialog.titleLabel.setFillParent(true)
            dialog.text(Label("\n" +
                    "Use the arrow keys to move.\n" +
                    "Use the mouse or displayed numerical key\nto choose options on screen.\n" +
                    "Use the escape key to quit.\n" +
                    "Collect coins to increase your score!",
                    skin, "black").apply {
                setFontScale(.75f)
            })

            dialog.button("Close")
            dialog.show(stageBg)
        }


        exit.onClick {
            Gdx.app.exit()
        }

        vertGroup.addActor(play)
        vertGroup.addActor(instructions)
        vertGroup.addActor(exit)
        Gdx.input.inputProcessor = stageBg

        scene = TITLE
    }

    override fun render() {
        when (scene) {
            PLAYING -> {
                world.setDelta(Gdx.graphics.deltaTime)
                world.process()
            }
            TITLE -> {
                Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
                Gdx.gl.glClear(
                        GL20.GL_COLOR_BUFFER_BIT
                                or GL20.GL_DEPTH_BUFFER_BIT
                                or (if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)
                )

                stageBg.act()
                stageBg.draw()
            }
        }
    }

    override fun dispose() {
        world.dispose()
    }
}
