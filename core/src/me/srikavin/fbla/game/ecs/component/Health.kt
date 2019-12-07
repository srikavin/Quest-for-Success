package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.physics.box2d.Body

class Health() : Component() {
    var health: Int = 0;
    var maxHealth: Int = 0;
}