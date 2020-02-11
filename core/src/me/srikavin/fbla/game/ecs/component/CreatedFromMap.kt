package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.artemis.annotations.EntityId
import me.srikavin.fbla.game.util.EntityInt

/**
 * Component indicating that an entity was the result of loading a map
 */
class CreatedFromMap : Component() {
    @EntityId
    var mapId: EntityInt = -1
}