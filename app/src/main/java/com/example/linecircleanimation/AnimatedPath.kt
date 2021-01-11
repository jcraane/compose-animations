package com.example.linecircleanimation

import android.animation.FloatArrayEvaluator
import android.graphics.PointF
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.DensityAmbient
import com.example.linecircleanimation.extensions.asFloat
import com.example.linecircleanimation.extensions.asPointF

private val value = FloatPropKey("value")

enum class TransitionState {
    START, END
}

private val definition = transitionDefinition<TransitionState> {
    state(TransitionState.START) {
        this[value] = 0f
    }

    state(TransitionState.END) {
        this[value] = 1f
    }

    transition(TransitionState.START to TransitionState.END, TransitionState.END to TransitionState.START) {
        value using tween(durationMillis = 1000)
    }
}

@Composable
fun AnimatedPath(shapeAnimation: ShapeAnimation) {
    WithConstraints() {
//        todo see of we can optimize this.

        val width = with(AmbientDensity.current) { constraints.maxWidth.toDp() }.value
        val height = with(AmbientDensity.current) { constraints.maxHeight.toDp() }.value

//        val pointsFrom = shapeAnimation.from.calculatePoints(width, height)
//        val pointsTo = shapeAnimation.to.calculatePoints(width, height)

        val state = transition(definition = definition, toState = shapeAnimation.toState, initState = shapeAnimation.fromState)

        val pointsFrom = if (shapeAnimation.fromState == TransitionState.START) shapeAnimation.from.calculatePoints(width, height) else shapeAnimation.to.calculatePoints(width, height)
        val pointsTo = if (shapeAnimation.fromState == TransitionState.START) shapeAnimation.to.calculatePoints(width, height) else shapeAnimation.from.calculatePoints(width, height)

        val animatedValue = state[value]
        val fae = FloatArrayEvaluator()
        val evaluatedValues = fae.evaluate(animatedValue, pointsFrom.asFloat().toFloatArray(), pointsTo.asFloat().toFloatArray())

        Path(points = evaluatedValues.toList().asPointF())
    }
}

@Composable
fun Path(points: List<PointF>) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val path = androidx.compose.ui.graphics.Path()
        val first = points.first()
        path.moveTo(first.x, first.y)
        points.drop(1).forEach { p -> path.lineTo(p.x, p.y) }
        drawPath(path, SolidColor(Color.Blue), style = Stroke(width = 20f))
    }
}