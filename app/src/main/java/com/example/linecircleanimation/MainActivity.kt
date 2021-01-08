package com.example.linecircleanimation

import android.animation.FloatArrayEvaluator
import android.graphics.PointF
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.graphics.rotationMatrix
import androidx.core.graphics.translationMatrix
import com.example.linecircleanimation.ui.LineCircleAnimationTheme
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val line = createLinePoints(600f)
        val circle = createCirclePoints(600f, 600f, 200f)

        val startXY = mutableListOf<Float>()
        val endXY = mutableListOf<Float>()
        line.forEach { p ->
            startXY += p.x
            startXY += p.y
        }
        circle.forEach { p ->
            endXY += p.x
            endXY += p.y
        }

        println()
        setContent {
            LineCircleAnimationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Column() {
                        var fromState by remember { mutableStateOf(LineState.LINE) }
                        var toState by remember { mutableStateOf(LineState.CIRCLE) }

                        Button(onClick = {
                            if (fromState == LineState.LINE) {
                                fromState = LineState.CIRCLE
                                toState = LineState.LINE
                            } else {
                                fromState = LineState.LINE
                                toState = LineState.CIRCLE
                            }
                        }) {
                            Text(text = "Animate")
                        }

                        AnimatedLine(startXY, endXY, fromState, toState)
                    }
                }
            }
        }
    }
}

private val value = FloatPropKey("value")

enum class LineState {
    LINE, CIRCLE
}

private val definition = transitionDefinition<LineState> {
    state(LineState.LINE) {
        this[value] = 0f
    }

    state(LineState.CIRCLE) {
        this[value] = 1f
    }

    transition(LineState.LINE to LineState.CIRCLE, LineState.CIRCLE to LineState.LINE) {
        value using tween(durationMillis = 1000)
    }
}

@Composable
fun AnimatedLine(
        startPoints: List<Float>,
        endPoints: List<Float>,
        fromState: LineState,
        toState: LineState) {

    val state = transition(definition = definition, initState = fromState, toState = toState)

    val animatedValue = state[value]
    val fae = FloatArrayEvaluator()
    val evaluatedValues = fae.evaluate(animatedValue, startPoints.toFloatArray(), endPoints.toFloatArray())
    val pointsToDraw = mutableListOf<PointF>()
    for (i in evaluatedValues.indices step 2) {
        pointsToDraw.add(PointF(evaluatedValues[i], evaluatedValues[i + 1]))
    }

    Line(pointsToDraw)
}

@Composable
fun Line() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val points = createLinePoints(this.size.width)
        val first = points.first()
        path.moveTo(first.x, first.y)
        points.drop(1).forEach { p -> path.lineTo(p.x, p.y) }
        drawPath(path, SolidColor(Color.Blue), style = Stroke(width = 20f))
    }
}

@Composable
fun Circle(height: Float, radius: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val points = createCirclePoints(this.size.width, height, radius)
        val first = points.first()
        path.moveTo(first.x, first.y)
        points.drop(1).forEach { p -> path.lineTo(p.x, p.y) }
        drawPath(path, SolidColor(Color.Blue), style = Stroke(width = 20f))
    }
}

@Composable
fun Line(points: List<PointF>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = Path()
        val first = points.first()
        path.moveTo(first.x, first.y)
        points.drop(1).forEach { p -> path.lineTo(p.x, p.y) }
        drawPath(path, SolidColor(Color.Blue), style = Stroke(width = 20f))
    }
}

private fun createLinePoints(width: Float): List<PointF> {
    return (0 until 80).toList()
            .map { index ->
                PointF((index * (width / 80)), 50f)
            }
}

private fun createCirclePoints(width: Float, height: Float, radius: Float): List<PointF> {
    val cx = width / 2
    val cy = height / 2

    val rotationMatrix = rotationMatrix(164f, cx, cy)
    val translationMatrix = translationMatrix(0f, 50f)

    val circlePart = 1.2 // 2.0 is a full circle
    val angle = circlePart * Math.PI / 80
    return (0 until 80).toList()
            .map { index ->
                val x = cx + (radius * cos(angle * index)).toFloat()
                val y = cy + (radius * sin(angle * index)).toFloat()
                val rotated = FloatArray(2)
                rotationMatrix.mapPoints(rotated, floatArrayOf(x, y))
                val translated = FloatArray(2)
                translationMatrix.mapPoints(translated, rotated)
                PointF(translated[0], translated[1])
            }
}

@Preview
@Composable
fun LinePreview() {
    LineCircleAnimationTheme {
        Line()
    }
}

@Preview
@Composable
fun CirclePreview() {
    LineCircleAnimationTheme {
        Circle(600f, 200f)
    }
}
