package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.util.EntityInt

enum class TutorialType {
    START,
    END
}

/**
 * Handles triggers resulting from player collision with tutorial hit boxes
 */
class TutorialTriggerHandler : TriggerHandler {
    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
//        val type =
    }
}