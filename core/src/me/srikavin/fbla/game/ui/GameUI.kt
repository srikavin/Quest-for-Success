package me.srikavin.fbla.game.ui

import com.badlogic.gdx.utils.Disposable

abstract class GameUI : Disposable {
    abstract fun render()
}