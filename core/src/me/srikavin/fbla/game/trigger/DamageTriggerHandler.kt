package me.srikavin.fbla.game.trigger

import com.artemis.World
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Filter
import com.badlogic.gdx.utils.TimeUtils
import ktx.math.times
import me.srikavin.fbla.game.ecs.component.*
import me.srikavin.fbla.game.ecs.system.TriggerSystem
import me.srikavin.fbla.game.map.MapLoader
import me.srikavin.fbla.game.state.GameState
import me.srikavin.fbla.game.util.EntityInt

private const val INVULNERABLE_TIME_MSEC: Long = 500

/**
 * Handles triggers resulting from player collision with coins.
 */
class DamageTriggerHandler : TriggerHandler {
    private val knockbackImpulse = Vector2(30f, 50f)
    private var lastDamageTime = 0L

    override fun run(world: World, player: EntityInt, triggerEntity: EntityInt, trigger: MapTrigger) {
        if (lastDamageTime + INVULNERABLE_TIME_MSEC > TimeUtils.millis()) {
            return
        }


        val gameState = world.getRegistered(GameState::class.java)
        val physicsMapper = world.getMapper(PhysicsBody::class.java)
        val transformMapper = world.getMapper(Transform::class.java)

        if (!physicsMapper.has(player) || !transformMapper.has(player)) {
            return
        }

        val pBody = physicsMapper[player].body
        val tBody = physicsMapper[triggerEntity].body

        val direction = pBody.position.sub(tBody.position).nor()

        pBody.applyLinearImpulse(knockbackImpulse.times(direction), transformMapper[player].position, true)

        if (gameState.gameRules.enemiesToGold && (trigger.properties.get("subtype", "", String::class.java) != "environmental")) {
            val mapLoader = world.getRegistered(MapLoader::class.java)
            mapLoader.createCoin(world, tBody.position)
            mapLoader.createCoin(world, tBody.position.cpy().add(1f, 1f))
            mapLoader.createCoin(world, tBody.position.cpy().add(-1f, 1f))
            mapLoader.createCoin(world, tBody.position.cpy().add(-1f, 0f))
            mapLoader.createCoin(world, tBody.position.cpy().add(-1f, -1f))
            world.delete(triggerEntity)
            return
        }

        val animationMapper = world.getMapper(SwitchableAnimation::class.java)

        gameState.lives -= 1

        animationMapper[player].lock("Damage")

        lastDamageTime = TimeUtils.millis()

        if (gameState.lives <= 0) {
            animationMapper[player].lock("Death")
            val filter = Filter()
            filter.groupIndex = -1
            pBody.fixtureList.forEach {
                it.filterData = filter
            }

            world.getEntity(player).edit().add(Dead().apply {
                respawnRunnable = {
                    val spawnPointMapper = world.getMapper(SpawnPoint::class.java)
                    if (spawnPointMapper.has(player) && physicsMapper.has(player)) {
                        val spawnPoint = spawnPointMapper[player].position.cpy()
                        world.delete(player)
                        world.getRegistered(MapLoader::class.java).createPlayer(world, spawnPoint)
                        world.getSystem(TriggerSystem::class.java).removePlayerFromCollision(player)
                        gameState.lives = 3
                    }
                }
            })
        }
    }
}