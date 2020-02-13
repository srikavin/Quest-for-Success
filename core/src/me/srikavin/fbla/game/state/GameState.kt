package me.srikavin.fbla.game.state

import me.srikavin.fbla.game.award.Award
import me.srikavin.fbla.game.award.Awards

data class GameState(
        var score: Int = 0,
        var lives: Int = 3,
        var gameRules: GameRules = GameRules(),
        val awards: LinkedHashSet<Award> = LinkedHashSet(4),
        var currentLevelPath: String = "assets/maps/level1.tmx") {
    @Transient
    var lastAddedAward: Award? = null

    fun addAward(name: String) {
        addAward(Awards.getAward(name))
    }

    fun addAward(award: Award) {
        awards.add(award)
        award.apply(gameRules)
        lastAddedAward = award
    }

    fun removeAward(name: String) {
        removeAward(Awards.getAward(name))
    }

    fun removeAward(award: Award) {
        awards.remove(award)
    }
}
