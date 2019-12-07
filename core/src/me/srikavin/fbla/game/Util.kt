package me.srikavin.fbla.game

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

typealias GdxArray<T> = Array<T>
typealias EntityInt = Int

private const val scaleFactor = 0.5f;

fun scaleToPhysics(x: Float): Float {
    return x * scaleFactor;
}

fun scaleToPhysics(x: Int): Float {
    return x * scaleFactor;
}


fun scaleToPhysics(vec: Vector2): Vector2 {
    return Vector2(scaleToPhysics(vec.x), scaleToPhysics(vec.y))
}