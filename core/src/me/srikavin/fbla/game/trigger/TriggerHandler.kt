package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger

interface TriggerHandler {
    fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger)
}