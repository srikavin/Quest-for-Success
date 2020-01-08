package me.srikavin.fbla.game

import com.badlogic.gdx.Input

/**
 * List of all possible actions to do within the game
 */
enum class Actions {
    MOVE_LEFT,
    MOVE_RIGHT,
    JUMP,
    USE,
    QUIT
}

/**
 * A list of bindings between actions and Gdx [Input] keys
 */
class KeyBindings(val bindings: Map<Actions, Int>) {
    constructor() : this(
            mapOf<Actions, Int>(
                    Pair(Actions.MOVE_LEFT, Input.Keys.LEFT),
                    Pair(Actions.MOVE_RIGHT, Input.Keys.RIGHT),
                    Pair(Actions.JUMP, Input.Keys.UP),
                    Pair(Actions.USE, Input.Keys.E),
                    Pair(Actions.QUIT, Input.Keys.ESCAPE)
            )
    )
}