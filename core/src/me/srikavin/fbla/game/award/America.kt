package me.srikavin.fbla.game.award

import me.srikavin.fbla.game.state.GameRules

class America : Award() {
    override fun apply(gameRules: GameRules) {
        gameRules.enemiesToGold = true
    }

    override fun getDescription(): String {
        return "You have achieved the BAA America Award! This marks the end of your journey and the beginning of something new!"
    }

    override fun getName(): String {
        return "america"
    }
}