package com.example.linecircleanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
import com.example.linecircleanimation.ui.LineCircleAnimationTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LineCircleAnimationTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Column() {
                        var shapeAnimation by remember { mutableStateOf<ShapeAnimation>(ShapeAnimation.Initial(Shape.Line()))}

                        Button(onClick = {
                            shapeAnimation = shapeAnimation.next()
                        }) {
                            Text(text = "Animate")
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        AnimatedPath(shapeAnimation)
                    }
                }
            }
        }
    }
}

sealed class ShapeAnimation(val from: Shape, val to: Shape, val fromState: TransitionState, val toState: TransitionState) {
    abstract fun next(): Animated

    class Initial(from: Shape) : ShapeAnimation(from, from, TransitionState.START, TransitionState.START) {
        override fun next() = Animated(from, nextShape(from), TransitionState.START, TransitionState.END)
    }

    class Animated(from: Shape, to: Shape, fromState: TransitionState, toState: TransitionState) : ShapeAnimation(from, to, fromState, toState) {
         override fun next(): Animated {
             val newFrom = this.to
             val newTo = nextShape(newFrom)
             val tempFromState = fromState
             return Animated(from = newFrom, to = newTo, fromState = toState, toState = tempFromState)
         }
    }

    companion object {
        fun nextShape(current: Shape) = when (current) {
            is Shape.Line -> Shape.Triangle()
            is Shape.Triangle -> Shape.Rectangle()
            is Shape.Rectangle -> Shape.Circle()
            is Shape.Circle -> Shape.Spiral()
            is Shape.Spiral -> Shape.Line()
        }
    }
}

