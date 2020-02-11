package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.EntityInt

/**
 * Handles triggers resulting from player collision with coins.
 */
class CoinTriggerHandler : TriggerHandler {
    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        world.delete(triggerEntity)

        val gameState = world.getRegistered(GameState::class.java)
        gameState.score += gameState.gameRules.coinMultiplier
        // TODO: Play Sound
    }
}