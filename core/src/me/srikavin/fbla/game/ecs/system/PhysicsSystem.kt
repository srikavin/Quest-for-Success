package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.managers.TagManager
import com.artemis.systems.IteratingSystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import ktx.collections.GdxArray
import me.srikavin.fbla.game.ecs.component.FixedRotation
import me.srikavin.fbla.game.ecs.component.MapTrigger
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Transform
import me.srikavin.fbla.game.graphics.player_foot_fixture_id
import me.srikavin.fbla.game.physics.ContactListenerManager


@All(Transform::class, PhysicsBody::class)
class PhysicsSystem(var physicsWorld: World, private val contactManager: ContactListenerManager) : IteratingSystem() {
    lateinit var transformMapper: ComponentMapper<Transform>
    lateinit var physicsMapper: ComponentMapper<PhysicsBody>
    lateinit var triggerMapper: ComponentMapper<MapTrigger>
    lateinit var fixedRotationMapper: ComponentMapper<FixedRotation>

    override fun initialize() {
        getSubscription().addSubscriptionListener(SubscriptionListener())
        physicsWorld.setContactListener(contactManager)
    }

    private inner class SubscriptionListener : EntitySubscription.SubscriptionListener {
        override fun inserted(entities: IntBag) {

            for (i in 0 until entities.size()) {
                val e = entities[i]

                val physics = physicsMapper[e]
                val transform = transformMapper[e]


                if (triggerMapper.has(e)) {
                    physics.isSensor = true
                }

                val bodyDef = BodyDef().apply {
                    type = physics.type
                    position.set(transform.position)
                    linearDamping = physics.linearDamping
                }
                physics.body = physicsWorld.createBody(bodyDef).apply {
                    userData = e
                }

                if (fixedRotationMapper.has(e)) {
                    physics.body.isFixedRotation = true
                }

                val fixtures: GdxArray<Fixture>

                if (physics.fixtureDefs.isEmpty) {
                    fixtures = GdxArray(false, 1)

                    val fixtureDef = FixtureDef().apply {
                        this.friction = physics.friction
                        this.density = physics.density
                        this.restitution = physics.restitution
                        this.shape = physics.shape
                    }

                    val fixture = physics.body.createFixture(fixtureDef)
                    fixture.userData = e
                    fixture.isSensor = physics.isSensor

                    fixtures.add(fixture)
                } else {
                    fixtures = GdxArray(false, physics.fixtureDefs.size)

                    for (fixtureDef in physics.fixtureDefs) {
                        val fixture = physics.body.createFixture(fixtureDef)
                        fixture.userData = e
                        fixture.isSensor = physics.isSensor

                        fixtures.add(fixture)
                    }
                }


                if (e == world.getSystem(TagManager::class.java).getEntityId("PLAYER")) {
                    val footBox = FixtureDef().apply {
                        this.isSensor = true
                        this.shape = PolygonShape().apply {
                            setAsBox(0.4f, 0.1f, Vector2(0f, -1f), 0f)
                        }
                    }

                    val fixture = physics.body.createFixture(footBox)
                    fixture.userData = player_foot_fixture_id
                    fixtures.add(fixture)
                }

                physics.fixtures = fixtures
            }
        }

        override fun removed(entities: IntBag) {
            for (i in 0 until entities.size()) {
                val e = entities[i]
                val physics = physicsMapper[e]

                physicsWorld.destroyBody(physics.body)
            }
        }
    }


    @Wire
    lateinit var camera: OrthographicCamera

    override fun begin() {
        physicsWorld.step(1 / 60f, 6, 6)
    }

    override fun process(entityId: Int) {
        transformMapper[entityId].position.set(physicsMapper[entityId].body.position)
    }
}