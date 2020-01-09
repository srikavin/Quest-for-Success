package me.srikavin.fbla.game

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
