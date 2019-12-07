package me.srikavin.fbla.game.ecs.component

import com.artemis.Component
import com.artemis.annotations.EntityId
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import me.srikavin.fbla.game.EntityInt

class CreatedFromMap() : Component() {
    @EntityId
    var mapId: EntityInt = -1
}