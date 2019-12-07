package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion

class Sprite : Component() {
    lateinit var sprite: TextureRegion
}
