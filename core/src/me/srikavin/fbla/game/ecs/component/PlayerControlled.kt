package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import me.srikavin.fbla.game.KeyBindings

/**
 * Component indicating that a sprite is player-controller with the contained keybindings
 */
class PlayerControlled : Component() {
    var bindings: KeyBindings = KeyBindings()
}