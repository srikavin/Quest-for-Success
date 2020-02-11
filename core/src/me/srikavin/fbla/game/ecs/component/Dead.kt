package me.srikavin.fbla.game.ecs.component

import com.artemis.Component

class Dead : Component() {
    var respawnRunnable: () -> Unit = {}
}