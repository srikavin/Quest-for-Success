package me.srikavin.fbla.game.physics

import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Manifold

/**
 * This class serves as the root physics contact listener. By default, it takes no actions. Instead, it delegates all
 * events to registered [ContactListener]s.
 */
class ContactListenerManager : ContactListener {
    private val listeners: MutableList<ContactListener> = ArrayList(2)

    /**
     * Register a contact listener to be called with every physics contact event
     *
     * @param listener The listener to register
     */
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