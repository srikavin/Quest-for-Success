package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.maps.MapProperties
import me.srikavin.fbla.game.trigger.TriggerType

class MapTrigger : Component() {
    lateinit var type: TriggerType
    var properties: MapProperties? = null
}