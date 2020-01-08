package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import me.srikavin.fbla.game.minigame.Minigame

class MinigameComponent : Component() {
    var minigame: Minigame? = null
}