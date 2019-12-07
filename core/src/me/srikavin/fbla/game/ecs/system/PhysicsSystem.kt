package me.srikavin.fbla.game.ecs.system

import com.artemis.ComponentMapper
import com.artemis.EntitySubscription
import com.artemis.annotations.All
import com.artemis.annotations.Wire
import com.artemis.systems.IteratingSystem
import com.artemis.utils.IntBag
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.*
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType
import ktx.collections.GdxArray
import ktx.collections.gdxArrayOf
import ktx.log.info
import me.srikavin.fbla.game.ecs.component.FixedRotation
import me.srikavin.fbla.game.ecs.component.PhysicsBody
import me.srikavin.fbla.game.ecs.component.Transform


@All(Transform::class, PhysicsBody::class)
class PhysicsSystem(var physicsWorld: World) : IteratingSystem() {
    lateinit var transformMapper: ComponentMapper<Transform>
    lateinit var physicsMapper: ComponentMapper<PhysicsBody>
    lateinit var fixedRotationMapper: ComponentMapper<FixedRotation>

    override fun initialize() {
        getSubscription().addSubscriptionListener(SubscriptionListener())
    }

    inner class SubscriptionListener : EntitySubscription.SubscriptionListener {
        override fun inserted(entities: IntBag) {
            for (i in 0 until entities.size()) {
                val e = entities[i]

                val physics = physicsMapper[e]
                val transform = transformMapper[e]


                val bodyDef = BodyDef().apply {
                    type = physics.type
                    position.set(transform.position)
                }
                physics.body = physicsWorld.createBody(bodyDef).apply {
                    userData = e
                }

                if (fixedRotationMapper.has(e)){
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

                    fixtures.add(fixture)
                } else {
                    info { "123123" }
                    fixtures = GdxArray(false, physics.fixtureDefs.size)

                    for (fixtureDef in physics.fixtureDefs) {
                        val fixture = physics.body.createFixture(fixtureDef)
                        fixture.userData = e

                        fixtures.add(fixture)
                    }
                }

                physics.fixtures = fixtures
            }
        }

        override fun removed(entities: IntBag) {
        }
    }


    class CollisionListener : ContactListener {
        override fun endContact(contact: Contact?) {
        }

        override fun beginContact(contact: Contact?) {
        }

        override fun preSolve(contact: Contact?, oldManifold: Manifold?) {
        }

        override fun postSolve(contact: Contact?, impulse: ContactImpulse?) {
        }
    }

    @Wire
    lateinit var camera: OrthographicCamera

    override fun begin() {
        physicsWorld.step(Gdx.graphics.deltaTime, 6, 2)
    }

    override fun process(entityId: Int) {
        transformMapper[entityId].position.set(physicsMapper[entityId].body.position)
    }
}