package me.srikavin.fbla.game.ecs.system

import com.artemis.BaseSystem
import com.artemis.ComponentMapper
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.badlogic.gdx.graphics.OrthographicCamera
import me.srikavin.fbla.game.ecs.component.Transform

/**
 * This system is responsible for moving the camera to follow the player.
 *
 * @param followHorizontal Whether or not to follow the x-position of the player
 * @param followVertical Whether or not to follow the y-position of the player
 */
class CameraFollowSystem(var followVertical: Boolean = true, var followHorizontal: Boolean = true) : BaseSystem() {
    @Wire
    private lateinit var transformMapper: ComponentMapper<Transform>
    @Wire
    internal lateinit var camera: OrthographicCamera

    override fun processSystem() {
        val player = world.getSystem(TagManager::class.java).getEntityId("PLAYER")

        if (player != -1 && transformMapper.has(player)) {
            val pos = transformMapper[player].position
            if (followHorizontal) {
                camera.position.x = pos.x

                // Avoid showing past the left-most boundaries of the map
                if (camera.position.x < 23f) {
                    camera.position.x = 23f
                }
            }

            if (followVertical) {
                camera.position.y = pos.y + 3.5f
            }
        }
    }
}