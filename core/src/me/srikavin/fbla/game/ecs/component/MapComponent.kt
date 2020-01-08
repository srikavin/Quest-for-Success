package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.maps.tiled.TiledMap

class MapComponent : Component() {
    lateinit var map: TiledMap
    var scaleFactor = 1f
}