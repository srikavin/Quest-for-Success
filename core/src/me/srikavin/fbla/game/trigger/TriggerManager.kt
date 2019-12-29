package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger

class TriggerManager {
    val coinTriggerHandler = CoinTriggerHandler()
    val transitionTriggerHandler = TransitionTriggerHandler()

    fun handle(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        when (trigger.type) {
            TriggerType.COIN -> coinTriggerHandler.run(world, player, triggerEntity, trigger)
            TriggerType.TRANSITION -> transitionTriggerHandler.run(world, player, triggerEntity, trigger)
        }
    }
}