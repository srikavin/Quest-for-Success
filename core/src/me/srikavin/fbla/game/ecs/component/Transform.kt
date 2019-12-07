package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.math.Vector2

class Transform : Component() {
    var position: Vector2 = Vector2(0f, 0f)
}