package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class SpawnPoint() : Component() {
    var position: Vector2 = Vector2()

    constructor(position: Vector2) : this() {
        this.position = position
    }
}