package me.srikavin.fbla.game.award

import me.srikavin.fbla.game.state.GameRules

class Business : Award() {
    override fun apply(gameRules: GameRules) {
        gameRules.coinMultiplier = 2
    }

    override fun getName(): String {
        return "business"
    }

    override fun getDescription(): String {
        return "You have achieved the BAA Business Award! Your focus on business has allowed you to double the value of future coins!"
    }
}