package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.physics.box2d.Body

class Map() : Component() {
    lateinit var map: TiledMap
    var scaleFactor = 1f
}