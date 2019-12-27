package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.World
import ktx.box2d.BodyDefinition
import ktx.box2d.create
import me.srikavin.fbla.game.ecs.component.MapTrigger


@All(MapTrigger::class)
class TriggerSystem : IteratingSystem() {
    private lateinit var triggerMapper: ComponentMapper<MapTrigger>

    @Wire
    lateinit var camera: OrthographicCamera

    @Wire
    lateinit var physicsWorld: World


    override fun initialize() {
        super.initialize()
        subscription.addSubscriptionListener(object : EntitySubscription.SubscriptionListener {
            override fun inserted(entities: IntBag?) {
                physicsWorld.create(BodyDefinition().apply { })
            }

            override fun removed(entities: IntBag?) {
            }
        })
    }

    override fun begin() {
    }

    override fun process(entityId: Int) {
    }
}
