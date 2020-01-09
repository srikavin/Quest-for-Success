package me.srikavin.fbla.game.trigger

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import me.srikavin.fbla.game.EntityInt
import me.srikavin.fbla.game.GameState
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.SpawnPoint
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.ecs.system.TriggerSystem
import me.srikavin.fbla.game.map.MapLoader

private const val INVULNERABLE_TIME_MSEC: Long = 500

/**
 * Handles triggers resulting from player collision with coins.
 */
class DamageTriggerHandler : TriggerHandler {
    private val knockbackImpulse = Vector2(-30f, 35f)
    private var lastDamageTime = 0L

    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        if (lastDamageTime + INVULNERABLE_TIME_MSEC > TimeUtils.millis()) {
            return
        }

        val gameState = world.getRegistered(GameState::class.java)
        val physicsMapper = world.getMapper(PhysicsBody::class.java)
        val transformMapper = world.getMapper(Transform::class.java)

        gameState.lives -= 1

        lastDamageTime = TimeUtils.millis()
        if (physicsMapper.has(player) && transformMapper.has(player)) {
            physicsMapper[player].body.applyLinearImpulse(knockbackImpulse, transformMapper[player].position, true)
        }

        if (gameState.lives <= 0) {
            val spawnPointMapper = world.getMapper(SpawnPoint::class.java)
            if (spawnPointMapper.has(player) && physicsMapper.has(player)) {
                val spawnPoint = spawnPointMapper[player].position.cpy()
                world.delete(player)
                world.getRegistered(MapLoader::class.java).createPlayer(world, spawnPoint)
                world.getSystem(TriggerSystem::class.java).removePlayerFromCollision(player)
                gameState.lives = 3
            }
        }
    }
}