package me.srikavin.fbla.game.physics

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

class ContactListenerManager : ContactListener {
    private val listeners: MutableList<ContactListener> = ArrayList(2)

    fun addListener(listener: ContactListener) {
        listeners.add(listener)
    }

    override fun endContact(contact: Contact) {
        listeners.forEach {
            it.endContact(contact)
        }
    }

    override fun beginContact(contact: Contact) {
        listeners.forEach {
            it.beginContact(contact)
        }
    }

    override fun preSolve(contact: Contact, oldManifold: Manifold) {
        listeners.forEach {
            it.preSolve(contact, oldManifold)
        }
    }

    override fun postSolve(contact: Contact, impulse: ContactImpulse) {
        listeners.forEach {
            it.postSolve(contact, impulse)
        }
    }
}