package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.OrthographicCamera
import me.srikavin.fbla.game.ecs.component.Transform

class CameraFollowSystem(val followVertical: Boolean = false, val followHorizontal: Boolean = true) : BaseSystem() {
    lateinit var transformMapper: ComponentMapper<Transform>

    @Wire
    lateinit var camera: OrthographicCamera

    override fun processSystem() {
        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

        if (transformMapper.has(player)) {
            val pos = transformMapper[player].position
            if (followHorizontal) {
                camera.position.x = pos.x
            }
            if (followVertical) {
                camera.position.y = pos.y
            }
        }
    }
}