package me.srikavin.fbla.game.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader

object GameFonts {
    /**
     * Size 22
     */
    lateinit var MENU: BitmapFont

    /**
     * Size 36
     */
    lateinit var DEFAULT: BitmapFont

    /**
     * Size 48
     */
    lateinit var LARGE: BitmapFont

    /**
     * Size 72
     */
    lateinit var HUGE: BitmapFont

    fun loadFonts(assetManager: AssetManager, path: String): Unit {
        assetManager.load("$path-menu.ttf", BitmapFont::class.java, parameter(path, 22))
        assetManager.load("$path-default.ttf", BitmapFont::class.java, parameter(path, 36))
        assetManager.load("$path-large.ttf", BitmapFont::class.java, parameter(path, 48))
        assetManager.load("$path-huge.ttf", BitmapFont::class.java, parameter(path, 72))


        MENU = assetManager.finishLoadingAsset("$path-menu.ttf")
        DEFAULT = assetManager.finishLoadingAsset("$path-default.ttf")
        LARGE = assetManager.finishLoadingAsset("$path-large.ttf")
        HUGE = assetManager.finishLoadingAsset("$path-huge.ttf")

        MENU.data.markupEnabled = true
        DEFAULT.data.markupEnabled = true
        LARGE.data.markupEnabled = true
        HUGE.data.markupEnabled = true
    }

    private fun parameter(path: String, s: Int): FreetypeFontLoader.FreeTypeFontLoaderParameter {
        return FreetypeFontLoader.FreeTypeFontLoaderParameter().apply {
            fontParameters.apply {
                size = s
                fontFileName = path
            }
            fontFileName = path
        }
    }
}