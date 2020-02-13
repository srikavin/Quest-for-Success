package me.srikavin.fbla.game.award

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import me.srikavin.fbla.game.state.GameRules

abstract class Award {
    private lateinit var texture: Texture
    private lateinit var drawable: TextureRegionDrawable

    abstract fun apply(gameRules: GameRules)

    abstract fun getName(): String

    fun loadDrawable(assetManager: AssetManager) {
        val path = "assets/graphics/awards/${getName()}.png"
        assetManager.load(path, Texture::class.java)
        texture = assetManager.finishLoadingAsset(path)
        drawable = TextureRegionDrawable(texture)
    }

    fun getTexture(): Texture {
        return texture
    }

    fun getDrawable(): TextureRegionDrawable {
        return drawable
    }

    abstract fun getDescription(): String

    override fun hashCode(): Int {
        return getName().hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Award && other.getName() == this.getName()
    }
}