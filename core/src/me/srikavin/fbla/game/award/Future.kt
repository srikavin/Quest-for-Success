package me.srikavin.fbla.game.award

import me.srikavin.fbla.game.state.GameRules

class Future : Award() {
    override fun apply(gameRules: GameRules) {
        gameRules.livesGainedPerLevel = 1
    }

    override fun getName(): String {
        return "future"
    }

    override fun getDescription(): String {
        return "You have achieved the BAA Future Award! Your focus on the future has allowed you to become a stronger individual, gaining new lives every step of the way!"
    }
}