package me.srikavin.fbla.game.minigame.dropcatch

import com.artemis.Component

enum class DropcatchItemType {
    GOOD,
    BAD
}

class DropcatchItemComponent : Component() {
    lateinit var type: DropcatchItemType
}