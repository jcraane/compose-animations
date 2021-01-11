package com.example.linecircleanimation.extensions

import android.graphics.PointF

fun List<PointF>.asFloat() = map { listOf(it.x, it.y) }.flatten()
fun List<Float>.asPointF() = chunked(2).map { PointF(it[0], it[1]) }