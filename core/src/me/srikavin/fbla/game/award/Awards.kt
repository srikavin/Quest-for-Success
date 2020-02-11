package me.srikavin.fbla.game.award

import com.badlogic.gdx.assets.AssetManager

object Awards {
    val future: Future = Future()
    val business: Business = Business()
    val leader: Leader = Leader()
    val america: America = America()

    private val nameMap = mapOf(
            "future" to future,
            "business" to business,
            "leader" to leader,
            "america" to america
    )

    fun init(assetManager: AssetManager) {
        future.loadDrawable(assetManager)
        business.loadDrawable(assetManager)
        leader.loadDrawable(assetManager)
        america.loadDrawable(assetManager)
    }

    fun getAward(name: String): Award {
        return nameMap.getValue(name)
    }
}