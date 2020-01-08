package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.maps.MapProperties
import me.srikavin.fbla.game.trigger.TriggerType

/**
 * Component indicating that an entity is a map trigger of a certain type
 */
class MapTrigger : Component() {
    /**
     * The type of map trigger
     */
    lateinit var type: TriggerType
    /**
     * The properties of the trigger defined inside of the map
     */
    var properties: MapProperties = MapProperties()
}