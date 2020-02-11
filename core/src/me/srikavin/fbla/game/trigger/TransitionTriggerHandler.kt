package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.EntityInt

/**
 * Handles triggers resulting from player collision with level transition triggers.
 */
class TransitionTriggerHandler : TriggerHandler {
    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        val mapLoader = world.getRegistered(MapLoader::class.java)
        val newMap = world.getMapper(MapTrigger::class.java)[triggerEntity].properties["level_name"]!!.toString()

        val gameState = world.getRegistered(GameState::class.java)
        gameState.lives += gameState.gameRules.livesGainedPerLevel
        gameState.currentLevelPath = newMap

        mapLoader.loadMap(world, newMap)
    }

}
