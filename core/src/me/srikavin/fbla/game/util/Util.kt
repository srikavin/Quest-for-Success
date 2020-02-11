package me.srikavin.fbla.game.util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.utils.Array
import me.srikavin.fbla.game.minigame.Minigame
import kotlin.reflect.KProperty

/**
 * Type alias to avoid mixing up Kotlin Arrays with Gdx Arrays
 */
typealias GdxArray<T> = Array<T>

/**
 * Type alias to maintain type safety with Artemis-ODB entity identifiers
 */
typealias EntityInt = Int


/**
 * Utility class to allow kotlin classes that delegate to map properties for type safety reasons
 */
class MapTriggerDelegate(val name: String) {
    operator fun getValue(mapTriggerProperties: Minigame.MapTriggerProperties, property: KProperty<*>): String {
        return mapTriggerProperties.properties.get(name)?.toString()
                ?: throw RuntimeException("Minigame trigger without `$name`!")

    }

    operator fun setValue(mapTriggerProperties: Minigame.MapTriggerProperties, property: KProperty<*>, value: String) {
        mapTriggerProperties.properties.put(name, value)
    }
}

fun unregisterInputHandler(processor: InputProcessor) {
    val cur = Gdx.input.inputProcessor ?: return

    if (cur == processor) {
        Gdx.input.inputProcessor = null
    }

    if (cur is InputMultiplexer && cur.processors.items.contains(processor)) {
        cur.removeProcessor(processor)
        return
    }
}

fun registerInputHandler(processor: InputProcessor) {
    val cur = Gdx.input.inputProcessor

    if (cur == null) {
        Gdx.input.inputProcessor = InputMultiplexer(processor)
        return
    }

    if (cur is InputMultiplexer) {
        if (cur.processors.items.contains(processor)) {
            // do nothing
            return
        }

        cur.addProcessor(processor)
    } else {
        if (cur != processor) {
            Gdx.input.inputProcessor = InputMultiplexer(cur, processor)
        }
        Gdx.input.inputProcessor = InputMultiplexer(cur)
    }
}
