package me.srikavin.fbla.game.ecs.component

import com.artemis.Component

/**
 * Contains information about ongoing tutorials.
 */
class TutorialComponent() : Component() {
    /**
     * A unique identifier to identify this tutorial segment
     */
    lateinit var id: String

    /**
     * The text to display to the user when this tutorial segment is active
     */
    lateinit var text: String

    constructor(id: String, text: String) : this() {
        this.text = text
        this.id = id
    }
}