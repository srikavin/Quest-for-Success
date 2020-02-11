package me.srikavin.fbla.game.award

import me.srikavin.fbla.game.state.GameRules

class Future : Award() {
    override fun apply(gameRules: GameRules) {
        gameRules.livesGainedPerLevel = 1
    }

    override fun getName(): String {
        return "future"
    }
}