package me.srikavin.fbla.game

import com.badlogic.gdx.Input

/**
 * List of all possible actions to do within the game
 */
enum class GameActions {
    MOVE_LEFT,
    MOVE_RIGHT,
    JUMP,
    USE,
    QUIT
}

/**
 * A list of bindings between actions and Gdx [Input] keys
 */
class KeyBindings(val bindings: Map<GameActions, Int>) {
    constructor() : this(
            mapOf<GameActions, Int>(
                    Pair(GameActions.MOVE_LEFT, Input.Keys.LEFT),
                    Pair(GameActions.MOVE_RIGHT, Input.Keys.RIGHT),
                    Pair(GameActions.JUMP, Input.Keys.UP),
                    Pair(GameActions.USE, Input.Keys.E),
                    Pair(GameActions.QUIT, Input.Keys.ESCAPE)
            )
    )
}