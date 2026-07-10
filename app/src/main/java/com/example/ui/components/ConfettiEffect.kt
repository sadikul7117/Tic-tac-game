package com.example.ui.components

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.isActive
import kotlin.random.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    val sizeW: Float,
    val sizeH: Float,
    val color: Color,
    var speedY: Float,
    var speedX: Float,
    var rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiEffect(modifier: Modifier = Modifier) {
    var sizeOfScreen by remember { mutableStateOf(Size.Zero) }
    val particles = remember { mutableStateListOf<ConfettiParticle>() }

    val colors = listOf(
        Color(0xFFFFC107), Color(0xFFFF5722), Color(0xFFE91E63),
        Color(0xFF9C27B0), Color(0xFF3F51B5), Color(0xFF00BCD4),
        Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFFFE082),
        Color(0xFF80DEEA), Color(0xFFF48FB1), Color(0xFFA5D6A7)
    )

    // Trigger generation on size changed
    LaunchedEffect(sizeOfScreen) {
        if (sizeOfScreen.width > 0f && sizeOfScreen.height > 0f) {
            particles.clear()
            repeat(80) {
                particles.add(
                    ConfettiParticle(
                        x = Random.nextFloat() * sizeOfScreen.width,
                        y = -Random.nextFloat() * sizeOfScreen.height * 0.8f,
                        sizeW = Random.nextFloat() * 12f + 8f,
                        sizeH = Random.nextFloat() * 24f + 12f,
                        color = colors.random(),
                        speedY = Random.nextFloat() * 300f + 200f, // pixels per second
                        speedX = (Random.nextFloat() - 0.5f) * 150f,
                        rotation = Random.nextFloat() * 360f,
                        rotationSpeed = (Random.nextFloat() - 0.5f) * 400f // degrees per sec
                    )
                )
            }
        }
    }

    // Physics Animation Loop
    LaunchedEffect(particles.size) {
        if (particles.isEmpty()) return@LaunchedEffect
        var lastTime = -1L
        while (isActive) {
            withInfiniteAnimationFrameMillis { frameTime ->
                if (lastTime == -1L) {
                    lastTime = frameTime
                }
                val dt = minOf(0.032f, (frameTime - lastTime) / 1000f) // limit max step to 30fps equivalence
                lastTime = frameTime

                for (p in particles) {
                    p.y += p.speedY * dt
                    p.x += p.speedX * dt
                    p.rotation += p.rotationSpeed * dt

                    // Reset if out of screen
                    if (p.y > sizeOfScreen.height) {
                        p.y = -50f
                        p.x = Random.nextFloat() * sizeOfScreen.width
                        p.speedY = Random.nextFloat() * 300f + 200f
                        p.speedX = (Random.nextFloat() - 0.5f) * 150f
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (sizeOfScreen != size) {
            sizeOfScreen = size
        }

        for (p in particles) {
            withTransform({
                translate(p.x, p.y)
                rotate(p.rotation, pivot = Offset(p.sizeW / 2, p.sizeH / 2))
            }) {
                drawRect(
                    color = p.color,
                    size = Size(p.sizeW, p.sizeH)
                )
            }
        }
    }
}
