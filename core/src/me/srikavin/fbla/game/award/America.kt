package me.srikavin.fbla.game.award

import me.srikavin.fbla.game.state.GameRules

class America : Award() {
    override fun apply(gameRules: GameRules) {
        gameRules.enemiesToGold = true
    }

    override fun getName(): String {
        return "america"
    }
}