package com.github.netkex.mindmap

import org.jetbrains.skia.Point
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

operator fun Point.plus(pt: Point): Point {
    return Point(x + pt.x, y + pt.y)
}

operator fun Point.minus(pt: Point): Point {
    return Point(x - pt.x, y - pt.y)
}

operator fun Point.times(a: Float): Point {
    return Point(x * a, y * a)
}

fun Point.len(): Float {
    return sqrt(x.pow(2) + y.pow(2))
}

fun Point.normalise(): Point {
    return this * (1 / this.len())
}

fun Point.rotate(alpha: Float): Point {
    return Point(
        x * cos(alpha) - y * sin(alpha),
        x * sin(alpha) + y * cos(alpha)
    )
}


