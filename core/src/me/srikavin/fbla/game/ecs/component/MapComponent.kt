package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.maps.tiled.TiledMap

/**
 * Component representing a loaded map, and its scale factor
 */
class MapComponent : Component() {
    lateinit var map: TiledMap
    var scaleFactor = 1f
}