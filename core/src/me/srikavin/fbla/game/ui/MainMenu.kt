package me.srikavin.fbla.game.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import ktx.actors.alpha
import me.srikavin.fbla.game.ext.addImageTextButton
import me.srikavin.fbla.game.ext.sequence
import me.srikavin.fbla.game.ext.table
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.GdxArray
import me.srikavin.fbla.game.util.SaveUtils
import me.srikavin.fbla.game.util.registerInputHandler
import me.srikavin.fbla.game.util.unregisterInputHandler

private const val style_name = "menu"

class MainMenu(private val skin: Skin, private val playRunnable: (GameState) -> Unit) : GameUI() {
    private val stage = Stage(ExtendViewport(1920f, 1080f, 1920f, 1080f))
    private val container = Table(skin)
    private val cloudContainer = Table(skin)
    private val bgStack = Stack()
    private val filterStack = Stack()

    private lateinit var submenu: Table
    private lateinit var infoPanel: Table

    private var currentMenu: Button? = null
    private var currentPanel: Button? = null

    private val curButtons: GdxArray<Button> = GdxArray(8)

    private val logo: Drawable
    private val fblaLogo: Drawable
    private val cloud: Drawable
    private val cloud2: Drawable
    private val cloud3: Drawable

    init {
        logo = TextureRegionDrawable(Texture(Gdx.files.internal("assets/graphics/titlelogopic.png")))
        fblaLogo = TextureRegionDrawable(Texture(Gdx.files.internal("assets/graphics/fbla-logo.png")))

        cloud = TextureRegionDrawable(Texture(Gdx.files.internal("assets/graphics/backgrounds/cloud1.png")))
        cloud2 = TextureRegionDrawable(Texture(Gdx.files.internal("assets/graphics/backgrounds/cloud2.png")))
        cloud3 = TextureRegionDrawable(Texture(Gdx.files.internal("assets/graphics/backgrounds/cloud3.png")))

        bgStack.add(Image(
                TextureRegionDrawable(Texture(Gdx.files.internal("assets/graphics/backgrounds/backgroundColorGrassTiled.png"))), Scaling.fill))

        stage.addActor(bgStack)
        stage.addActor(cloudContainer)
        stage.addActor(filterStack)
        stage.addActor(container)
        registerInputHandler(stage)
    }

    var clickAgain = false

    fun build() {
        stage.viewport.update(Gdx.graphics.width, Gdx.graphics.height)

        currentPanel = null
        currentMenu = null

        bgStack.setFillParent(true)
        bgStack.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        filterStack.setFillParent(true)
        filterStack.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        container.setFillParent(true)
        container.setSize(Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())

        curButtons.clear()
        container.clear()
        cloudContainer.clear()

        fun animateCloud(cloud: Drawable, duration: Float, offset: Float, vOffset: Float) {
            val actor = Image(cloud)
            cloudContainer.addActor(actor)

            actor.addAction(Actions.repeat(RepeatAction.FOREVER,
                    Actions.sequence(
                            Actions.moveTo(2000f + offset, 750 + vOffset),
                            Actions.moveTo(-700f, 700f + vOffset, duration)
                    ))
            )
        }

        animateCloud(cloud, 25f, 300f, 100f)
        animateCloud(cloud2, 17f, 0f, 100f)
        animateCloud(cloud3, 45f, 700f, -120f)
        animateCloud(cloud, 35f, 500f, 0f)

        container.left()
        container.add().width(Gdx.graphics.width / 15f)

        val width = 230f

        container.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) { t ->
            t.defaults().width(width).height(70f)

            buttons(t,
                    Buttoni("Play", null,
                            Buttoni("New Game", null, Runnable {
                                if (!clickAgain && SaveUtils.saveGameExists()) {
                                    infoPanel.clearChildren()
                                    infoPanel.add(Label("[accent]Warning[]", skin, "default"))
                                    infoPanel.row()
                                    infoPanel.add("This will overwrite your previous save game!\nClick again to confirm.", style_name)
                                    clickAgain = true
                                } else {
                                    playRunnable(GameState())
                                }
                            }),
                            Buttoni("Load Game", null, Runnable {
                                if (SaveUtils.saveGameExists()) {
                                    val gameState = GameState()
                                    SaveUtils.loadGame(gameState)
                                    playRunnable(gameState)
                                } else {
                                    infoPanel.clearChildren()
                                    infoPanel.add(Label("[accent]Warning[]", skin, "default"))
                                    infoPanel.row()
                                    infoPanel.add("There are no found save games!", style_name)
                                    clickAgain = true
                                }
                            })
                    ),
                    Buttoni("Instructions", null, Runnable {
                        infoPanel.clearChildren()
                        infoPanel.add(Label("[accent]Instructions[]", skin, "default"))
                        infoPanel.row()
                        infoPanel.add("Use the arrow keys to move.\n" +
                                "Use the mouse or displayed numerical key\nto choose options on screen.\n" +
                                "Use the escape key to quit.\n" +
                                "Collect coins to increase your score!", style_name)
                    }),
                    Buttoni("About", null,
                            Buttoni("Credits", null, Runnable {
                                infoPanel.clearChildren()
                                infoPanel.add("The [accent]Quest[] for [#00ff00]Success[]\n\n" +
                                        "Programming by [accent]Srikavin Ramkumar[]\n" +
                                        "Story by [#00ff00]David Rogers[]\n" +
                                        "Art by [#DA680F]Nicholas Quach[]\n\n" +
                                        "Made for the [#32527B]FBLA[] Computer Game &\nSimulation Programming Competition",
                                        style_name)
                            }),
                            Buttoni("License", null, Runnable {
                                infoPanel.clearChildren()
                                infoPanel.add("This game is licensed under the\n" +
                                        "[accent]Apache 2.0 License[].\n" +
                                        "A copy can be found in [#32527B]LICENSE.txt[]",
                                        style_name)
                            })
                    ),
                    Buttoni("Exit", null, Runnable { Gdx.app.exit() })

            )
        }.width(width).padLeft(7f).growY()

        container.table(NinePatchDrawable(skin.getPatch("menu-button-bg"))) { t ->
            submenu = t
            t.color.a = 0f
            t.top()
            t.defaults().width(width).height(70f)
        }.width(width).growY()

        container.add().width((Gdx.graphics.width - (Gdx.graphics.width / 10f + width * 2) - 800) / 2)

        val infoContainer = container
                .table {
                    it.top().add(Image(logo)).height(400f).width(400 * 166 / 133f)
                }.height(550f)
                .width(800f)
                .pad(20f)
                .top()
                .right().actor

        infoContainer.row()

        infoContainer.table {
            infoPanel = it.table(NinePatchDrawable(skin.getPatch("menu-button-bg")))
                    .width(550f)
                    .padTop(((Gdx.graphics.height - 170f) / 2) - 350f)
                    .growY().actor
        }.height(350f)

        infoContainer.row()

        infoContainer.table {
            it.bottom().add(Image(fblaLogo)).height(300f).width(300 * 8000 / 4500f)
                    .padTop(50f)
        }

        infoPanel.alpha = 0f
    }

    private fun fadeInMenu() {
        submenu.clearActions()
        submenu.sequence(Actions.alpha(1f, 0.35f, Interpolation.fade))
    }

    private fun fadeOutInfoPanel() {
        infoPanel.clearActions()
        infoPanel.sequence(
                Actions.alpha(1f),
                Actions.alpha(0f, 0.3f, Interpolation.fade),
                Actions.run { infoPanel.clearChildren() }
        )
    }

    private fun fadeInInfoPanel() {
        infoPanel.clearActions()
        infoPanel.sequence(Actions.alpha(1f, 0.35f, Interpolation.fade))
    }

    private fun fadeOutMenu() {
        if (submenu.children.isEmpty) {
            return
        }
        submenu.clearActions()
        submenu.sequence(
                Actions.alpha(1f),
                Actions.alpha(0f, 0.3f, Interpolation.fade),
                Actions.run { submenu.clearChildren() }
        )
    }


    private fun buttons(t: Table, vararg buttons: Buttoni) {
        for (b in buttons) {
            val out: Array<Button?> = arrayOf(null)
            out[0] = t.addImageTextButton(b.text, b.icon, Runnable {
                if (currentPanel == out[0]) {
                    currentPanel = null
                    fadeOutInfoPanel()
                } else if (currentPanel == null && b.runnable != null) {
                    currentPanel = out[0]
                    infoPanel.clearChildren()
                    fadeInInfoPanel()
                } else if (b.runnable != null) {
                    currentPanel = out[0]
                    infoPanel.clearChildren()
                }

                if (currentMenu == out[0]) {
                    currentMenu = null
                    fadeOutMenu()
                } else {
                    if (b.submenu != null) {
                        currentMenu = out[0]
                        submenu.clearChildren()

                        fadeInMenu()

                        submenu.add().padTop(container.getY(Align.topLeft) - out[0]!!.getY(Align.topLeft) - 70f)
                        submenu.row()
                        buttons(submenu, *b.submenu)
                    } else {
                        currentMenu = null
                        fadeOutMenu()
                        b.runnable?.run()
                    }
                }
            }, style_name).actor
            t.row()

            out[0]!!.padLeft(11f)
            curButtons.add(out[0]!!)
        }
    }

    private class Buttoni {
        val icon: Drawable?
        val text: String
        val runnable: Runnable?
        val submenu: Array<out Buttoni>?

        constructor(text: String, icon: Drawable?, runnable: Runnable) {
            this.icon = icon
            this.text = text
            this.runnable = runnable
            submenu = null
        }

        constructor(text: String, icon: Drawable?, vararg buttons: Buttoni) {
            this.icon = icon
            this.text = text
            runnable = null
            submenu = buttons
        }
    }

    override fun render() {
        curButtons.forEach {
            it.isChecked = currentMenu == it
        }

        submenu.isVisible = submenu.hasChildren()

        stage.act()
        stage.draw()
    }

    override fun dispose() {
        unregisterInputHandler(stage)
        stage.dispose()
    }
}