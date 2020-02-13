package me.srikavin.fbla.game.minigame

import com.artemis.World
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapProperties
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.MapTriggerDelegate

/**
 * The baseclass that all minigames inherit from. This class handles level transitions and communications with outside
 * systems. The field [mapProperties] is available to subclasses as a type-safe version of MapProperties.
 */
abstract class Minigame {
    /**
     * Stores whether the minigame is currently being played by the user.
     */
    var active: Boolean = false
        private set

    private var nextLevel: String = ""

    /**
     *  If a new map is loaded by a subclass, make sure to set [MapLoader.loadMap] with [MapLoader.UnloadType.NonMinigame]
     *  to avoid unloading this minigame instance.
     */
    protected lateinit var mapLoader: MapLoader

    /**
     * The entity-component-system world that can be used to create new entities.
     */
    protected lateinit var world: World

    /**
     * A type safe version of the MapProperties provided within the trigger object in the map.
     */
    protected lateinit var mapProperties: MapTriggerProperties

    class MapTriggerProperties(val properties: MapProperties) {
        val type: String by MapTriggerDelegate("type")
        val subtype: String by MapTriggerDelegate("subtype")
        val minigameType: String by MapTriggerDelegate("minigame_type")
        val nextLevel: String by MapTriggerDelegate("next_level")
        val message: String by MapTriggerDelegate("message")
    }

    /**
     * Reset the minigame to its initial conditions. This will always be called before [initialize].
     */
    fun reset(properties: MapProperties, world: World, mapLoader: MapLoader) {
        this.mapProperties = MapTriggerProperties(properties)
        this.nextLevel = mapProperties.nextLevel
        this.mapLoader = mapLoader
        this.world = world

        resetMinigame(properties)
    }

    /**
     * Reset the minigame to its initial conditions. This will be called by [reset]
     */
    protected abstract fun resetMinigame(properties: MapProperties)

    /**
     * Any inital UI initialization should occur here. The stage will not be modified outside of the minigame while it
     * is active.
     */
    fun initialize(skin: Skin, stage: Stage) {
        active = true
        initializeMinigame(skin, stage)
    }

    var awardTimer = 0f
    var awardActive = false

    /**
     * Ends the minigame and transitions to the next level.
     * If an award is to be shown, it will be shown before the next level.
     */
    fun endMinigame() {
        if (mapProperties.properties.containsKey("award")) {
            world.getRegistered(GameState::class.java).addAward(mapProperties.properties["award"].toString())
            awardTimer = 10f
            awardActive = true
        } else {
            exit()
        }
    }

    protected fun exit() {
        val gameState = world.getRegistered(GameState::class.java)
        gameState.lives += gameState.gameRules.livesGainedPerLevel
        gameState.currentLevelPath = "assets/maps/$nextLevel"

        awardActive = false
        active = false

        Gdx.app.postRunnable {
            mapLoader.loadMap(world, "assets/maps/$nextLevel")
        }
    }

    protected abstract fun initializeMinigame(skin: Skin, stage: Stage)

    /**
     * The stage will not be modified outside of the minigame while the minigame remains active. No references to the
     * stage should be kept within the minigame as they may be reused in other minigames after being cleared.
     */
    abstract fun render(camera: OrthographicCamera, batch: SpriteBatch, stage: Stage)

    /**
     * If true, the screen will not be cleared before [render] is called, if false the screen will be cleared before
     * [render] is called.
     */
    abstract fun shouldRenderBackground(): Boolean

    fun process(delta: Float) {
        if (awardTimer > 0f) {
            awardTimer -= delta
        } else if (awardTimer <= 0f && awardActive) {
            exit()
        } else {
            this.processMinigame(delta)
        }
    }

    /**
     * Any updates to the minigame should be processed here.
     *
     * @param delta The time that has passed since the last call to this function
     */
    abstract fun processMinigame(delta: Float)

    /**
     * If true, input will still affect the player. If false, the player movement cannot be affected while the minigame
     * is active. This value should remain constant after initialization.
     */
    open fun allowPlayerMovement(): Boolean {
        return false
    }
}

