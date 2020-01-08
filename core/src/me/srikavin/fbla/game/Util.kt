package me.srikavin.fbla.game

import com.badlogic.gdx.utils.Array

/**
 * Type alias to avoid mixing up Kotlin Arrays with Gdx Arrays
 */
typealias GdxArray<T> = Array<T>

/**
 * Type alias to maintain type safety with Artemis-ODB entity identifiers
 */
typealias EntityInt = Int