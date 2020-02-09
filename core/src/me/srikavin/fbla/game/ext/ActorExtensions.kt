package me.srikavin.fbla.game.ext

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction
import ktx.log.info

fun Actor.sequence(vararg actions: Action) {
    info { "${Actions.sequence(*actions, RemoveAction())}" }
    addAction(Actions.sequence(*actions, RemoveAction()))
}

fun Actor.parallel(vararg actions: Action) {
    addAction(Actions.parallel(*actions, RemoveAction()))
}