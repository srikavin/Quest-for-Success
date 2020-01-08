package me.srikavin.fbla.game.trigger

import com.artemis.World
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.ecs.component.MapTrigger

/**
 * Represents a handler for triggers.
 */
interface TriggerHandler {
    /**
     * This method will be called when an associated trigger is in collision with the player. To avoid repeated calls to
     * this method, call [World.delete] with [triggerEntity] to.
     *
     * @param world The world instance the game is currently using
     * @param player The id of the player entity
     * @param triggerEntity The id of the trigger entity
     * @param trigger The [MapTrigger] component associated with the [triggerEntity]
     */
    fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger)
}