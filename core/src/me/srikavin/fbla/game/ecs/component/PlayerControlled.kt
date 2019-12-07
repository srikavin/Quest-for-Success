package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import me.srikavin.fbla.game.KeyBindings

class PlayerControlled : Component() {
    var bindings: KeyBindings = KeyBindings()
}