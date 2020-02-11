package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.util.EntityInt

/**
 * Handles delegation of triggers to their associated [TriggerHandler].
 */
class TriggerManager {
    private val coinTriggerHandler = CoinTriggerHandler()
    private val transitionTriggerHandler = TransitionTriggerHandler()
    private val interactiveTriggerHandler = InteractiveTriggerHandler()
    private val minigameTriggerHandler = MinigameTriggerHandler()
    private val damageTriggerHandler = DamageTriggerHandler()
    private val levelTitleTriggerHandler = LevelTitleTriggerHandler()

    fun handle(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        when (trigger.type) {
            TriggerType.COIN -> coinTriggerHandler.run(world, player, triggerEntity, trigger)
            TriggerType.TRANSITION -> transitionTriggerHandler.run(world, player, triggerEntity, trigger)
            TriggerType.INTERACTIVE -> interactiveTriggerHandler.run(world, player, triggerEntity, trigger)
            TriggerType.MINIGAME -> minigameTriggerHandler.run(world, player, triggerEntity, trigger)
            TriggerType.DAMAGE -> damageTriggerHandler.run(world, player, triggerEntity, trigger)
//            TriggerType.TUTORIAL -> TODO()
            TriggerType.LEVEL_TITLE -> levelTitleTriggerHandler.run(world, player, triggerEntity, trigger)
        }
    }

    fun tick(world: World) {
        levelTitleTriggerHandler.tick(world)
    }
}