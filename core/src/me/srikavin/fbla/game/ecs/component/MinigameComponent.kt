package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import me.srikavin.fbla.game.minigame.Minigame

/**
 * Component containing a minigame
 *
 * @see me.srikavin.fbla.game.ecs.system.MinigameSystem
 * @see me.srikavin.fbla.game.ecs.system.MinigameRenderSystem
 */
class MinigameComponent : Component() {
    var minigame: Minigame? = null
}