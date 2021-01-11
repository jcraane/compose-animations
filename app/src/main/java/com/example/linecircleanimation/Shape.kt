package com.example.linecircleanimation

import android.graphics.Point
import android.graphics.PointF
import androidx.core.graphics.rotationMatrix
import androidx.core.graphics.translationMatrix
import kotlin.math.cos
import kotlin.math.sin

sealed class Shape(protected val numberOfPoints: Int = 300, val closePath: Boolean = false) {
    class Line : Shape() {
        override fun calculatePoints(width: Float, height: Float): List<PointF> {
            val midX = height / 2
            return (0 until numberOfPoints).toList()
                    .map { index ->
                        PointF((index * (width / numberOfPoints)), midX)
                    }
        }
    }

    class Triangle : Shape(closePath = true) {
        override fun calculatePoints(width: Float, height: Float): List<PointF> {
            val sides = 3
            val pointsPerSide = numberOfPoints / sides
            val heightUnit = height / pointsPerSide
            val widthUnit = width / pointsPerSide
            val midY = height / 2
            val midYUnit = midY / pointsPerSide

            return (0 until sides).toList().map { side ->
                (0 until pointsPerSide).toList().mapNotNull { index ->
                    when (side) {
                        0 -> PointF(width, heightUnit * index)
                        1 -> PointF(width - (widthUnit * index), height - (midYUnit * index))
                        2 -> PointF(widthUnit * index, midY - (midYUnit * index))
                        else -> null
                    }
                }
            }.flatten()
        }
    }

    class Rectangle : Shape(closePath = true) {
        override fun calculatePoints(width: Float, height: Float): List<PointF> {
            val sides = 4
            val pointsPerSide = numberOfPoints / sides
            val widthUnit = width / pointsPerSide
            val heightUnit = height / pointsPerSide

            return (0 until sides).toList().map { side ->
                (0 until pointsPerSide).toList().mapNotNull { index ->
                    when (side) {
                        0 -> PointF(widthUnit * index, 0f)
                        1 -> PointF(width, (heightUnit * index))
                        2 -> PointF(width - (widthUnit * index), height)
                        3 -> PointF(0f, height - (heightUnit * index))
                        else -> null
                    }
                }
            }.flatten()
        }
    }

    class Circle : Shape() {
        override fun calculatePoints(width: Float, height: Float): List<PointF> {
            val cx = width / 2
            val cy = height / 2
            val radius = width / 2

            val angle = (2.1 * Math.PI) / numberOfPoints

            return (0 until numberOfPoints).toList()
                    .map { index ->
                        val x = cx + (radius * cos(angle * index)).toFloat()
                        val y = cy + (radius * sin(angle * index)).toFloat()
                        PointF(x, y)
                    }
        }
    }

    class Spiral : Shape() {
        override fun calculatePoints(width: Float, height: Float): List<PointF> {
            val cx = width / 2
            val cy = height / 2
            val endRadius = width / 2

            val numberOfSpirals = 4
            val pointsPerSpiral = numberOfPoints / numberOfSpirals

            val rotationMatrix = rotationMatrix(164f, cx, cy)
            val translationMatrix = translationMatrix(0f, 0f)

            val circlePart = 2.0
            val angle = circlePart * Math.PI / pointsPerSpiral
            var radius = 0f
            val radiusUnit = endRadius / numberOfPoints

            return (0 until numberOfPoints).toList()
                    .map { index ->
                        val spiralPoint = index % pointsPerSpiral
                        val x = cx + (radius * cos(angle * spiralPoint)).toFloat()
                        val y = cy + (radius * sin(angle * spiralPoint)).toFloat()
                        val rotated = FloatArray(2)
                        rotationMatrix.mapPoints(rotated, floatArrayOf(x, y))
                        val translated = FloatArray(2)
                        translationMatrix.mapPoints(translated, rotated)
                        radius += radiusUnit
                        PointF(translated[0], translated[1])
                    }
        }
    }

    abstract fun calculatePoints(width: Float, height: Float): List<PointF>

    companion object {
        const val yOffset = 50f
    }
}