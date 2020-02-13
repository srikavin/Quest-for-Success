package me.srikavin.fbla.game.award

import me.srikavin.fbla.game.state.GameRules

class Leader : Award() {
    override fun apply(gameRules: GameRules) {
        gameRules.enemiesToGold = true
    }

    override fun getName(): String {
        return "leader"
    }

    override fun getDescription(): String {
        return "You have achieved the BAA Leader Award! Your focus on leadership has inspired your enemies to support you, giving you coins when you meet them!"
    }
}