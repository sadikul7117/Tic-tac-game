package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun DrawX(
    modifier: Modifier = Modifier,
    color: Color,
    style: String = "classic",
    isUltra: Boolean = true
) {
    // Animation of piece drawing
    val animateVal = remember { Animatable(0f) }
    LaunchedEffect(key1 = style) {
        animateVal.animateTo(1f, animationSpec = tween(350))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val padding = w * 0.15f
        
        val startX1 = padding
        val startY1 = padding
        val endX1 = w - padding
        val endY1 = h - padding

        val startX2 = w - padding
        val startY2 = padding
        val endX2 = padding
        val endY2 = h - padding

        val t = animateVal.value

        when (style.lowercase()) {
            "neon" -> {
                // Neon glow layer
                val strokeGlow = w * 0.18f
                val glowColor = color.copy(alpha = 0.25f)
                
                // Draw glow first diagonal
                if (t > 0f) {
                    val currentEndVal = t * 2f
                    val line1Progress = minOf(1f, currentEndVal)
                    drawLine(
                        color = glowColor,
                        start = Offset(startX1, startY1),
                        end = Offset(startX1 + (endX1 - startX1) * line1Progress, startY1 + (endY1 - startY1) * line1Progress),
                        strokeWidth = strokeGlow,
                        cap = StrokeCap.Round
                    )
                    
                    if (t > 0.5f) {
                        val line2Progress = (t - 0.5f) * 2f
                        drawLine(
                            color = glowColor,
                            start = Offset(startX2, startY2),
                            end = Offset(startX2 + (endX2 - startX2) * line2Progress, startY2 + (endY2 - startY2) * line2Progress),
                            strokeWidth = strokeGlow,
                            cap = StrokeCap.Round
                        )
                    }
                }
                
                // Core neon line
                val strokeCore = w * 0.08f
                if (t > 0f) {
                    val currentEndVal = t * 2f
                    val line1Progress = minOf(1f, currentEndVal)
                    drawLine(
                        color = Color.White, // bright core
                        start = Offset(startX1, startY1),
                        end = Offset(startX1 + (endX1 - startX1) * line1Progress, startY1 + (endY1 - startY1) * line1Progress),
                        strokeWidth = strokeCore,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = color, // overlay accent
                        start = Offset(startX1, startY1),
                        end = Offset(startX1 + (endX1 - startX1) * line1Progress, startY1 + (endY1 - startY1) * line1Progress),
                        strokeWidth = strokeCore * 0.6f,
                        cap = StrokeCap.Round
                    )
                    
                    if (t > 0.5f) {
                        val line2Progress = (t - 0.5f) * 2f
                        drawLine(
                            color = Color.White,
                            start = Offset(startX2, startY2),
                            end = Offset(startX2 + (endX2 - startX2) * line2Progress, startY2 + (endY2 - startY2) * line2Progress),
                            strokeWidth = strokeCore,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = color,
                            start = Offset(startX2, startY2),
                            end = Offset(startX2 + (endX2 - startX2) * line2Progress, startY2 + (endY2 - startY2) * line2Progress),
                            strokeWidth = strokeCore * 0.6f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
            
            "brush" -> {
                // Calligraphy/brush effect with slightly irregular paths and tapered lines
                val strokeWidth = w * 0.07f
                val path1 = Path().apply {
                    moveTo(startX1, startY1)
                    quadraticTo(
                        startX1 + (endX1 - startX1) * 0.4f, 
                        startY1 + (endY1 - startY1) * 0.5f + (w * 0.04f), 
                        startX1 + (endX1 - startX1) * t, 
                        startY1 + (endY1 - startY1) * t
                    )
                }
                drawPath(
                    path = path1,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                if (t > 0.5f) {
                    val t2 = (t - 0.5f) * 2f
                    val path2 = Path().apply {
                        moveTo(startX2, startY2)
                        quadraticTo(
                            startX2 + (endX2 - startX2) * 0.6f, 
                            startY2 + (endY2 - startY2) * 0.4f - (w * 0.04f), 
                            startX2 + (endX2 - startX2) * t2, 
                            startY2 + (endY2 - startY2) * t2
                        )
                    }
                    drawPath(
                        path = path2,
                        color = color,
                        style = Stroke(width = strokeWidth * 0.9f, cap = StrokeCap.Round)
                    )
                }
            }

            "pixel" -> {
                // Retro 8-bit blocky style
                val gridBlocks = 7
                val blockSizeW = (w - padding * 2) / gridBlocks
                val blockSizeH = (h - padding * 2) / gridBlocks
                
                val blocksToFill = listOf(
                    0 to 0, 1 to 1, 2 to 2, 3 to 3, 4 to 4, 5 to 5, 6 to 6,
                    6 to 0, 5 to 1, 4 to 2, 2 to 4, 1 to 5, 0 to 6
                )

                val countToDraw = (blocksToFill.size * t).toInt()
                
                for (idx in 0 until countToDraw) {
                    val (col, row) = blocksToFill[idx]
                    val drawX = padding + col * blockSizeW
                    val drawY = padding + row * blockSizeH
                    drawRect(
                        color = color,
                        topLeft = Offset(drawX, drawY),
                        size = Size(blockSizeW * 0.9f, blockSizeH * 0.9f)
                    )
                }
            }

            else -> {
                // Classic Slate/Minimal style (with gorgeous 3D shadows & highlights in Ultra mode!)
                val strokeWidth = w * 0.08f
                if (t > 0f) {
                    val currentEndVal = t * 2f
                    val line1Progress = minOf(1f, currentEndVal)
                    
                    if (isUltra) {
                        // 1. Drop shadow (3D depth layer)
                        val shadowOffset = w * 0.03f
                        drawLine(
                            color = Color.Black.copy(alpha = 0.2f),
                            start = Offset(startX1 + shadowOffset, startY1 + shadowOffset),
                            end = Offset(startX1 + (endX1 - startX1) * line1Progress + shadowOffset, startY1 + (endY1 - startY1) * line1Progress + shadowOffset),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }

                    // 2. Base line
                    drawLine(
                        color = color,
                        start = Offset(startX1, startY1),
                        end = Offset(startX1 + (endX1 - startX1) * line1Progress, startY1 + (endY1 - startY1) * line1Progress),
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )

                    if (isUltra) {
                        // 3. High-definition glossy specular highlight reflection
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(startX1, startY1),
                            end = Offset(startX1 + (endX1 - startX1) * line1Progress, startY1 + (endY1 - startY1) * line1Progress),
                            strokeWidth = strokeWidth * 0.25f,
                            cap = StrokeCap.Round
                        )
                    }
                    
                    if (t > 0.5f) {
                        val line2Progress = (t - 0.5f) * 2f
                        
                        if (isUltra) {
                            // 1. Drop shadow for second stroke
                            val shadowOffset = w * 0.03f
                            drawLine(
                                color = Color.Black.copy(alpha = 0.2f),
                                start = Offset(startX2 + shadowOffset, startY2 + shadowOffset),
                                end = Offset(startX2 + (endX2 - startX2) * line2Progress + shadowOffset, startY2 + (endY2 - startY2) * line2Progress + shadowOffset),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round
                            )
                        }

                        // 2. Base line for second stroke
                        drawLine(
                            color = color,
                            start = Offset(startX2, startY2),
                            end = Offset(startX2 + (endX2 - startX2) * line2Progress, startY2 + (endY2 - startY2) * line2Progress),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )

                        if (isUltra) {
                            // 3. Specular highlight line for second stroke
                            drawLine(
                                color = Color.White.copy(alpha = 0.5f),
                                start = Offset(startX2, startY2),
                                end = Offset(startX2 + (endX2 - startX2) * line2Progress, startY2 + (endY2 - startY2) * line2Progress),
                                strokeWidth = strokeWidth * 0.25f,
                                cap = StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawO(
    modifier: Modifier = Modifier,
    color: Color,
    style: String = "classic",
    isUltra: Boolean = true
) {
    val animateVal = remember { Animatable(0f) }
    LaunchedEffect(key1 = style) {
        animateVal.animateTo(1f, animationSpec = tween(350))
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val padding = w * 0.16f
        val radius = (w / 2f) - padding
        val t = animateVal.value

        when (style.lowercase()) {
            "neon" -> {
                val glowColor = color.copy(alpha = 0.25f)
                val strokeGlow = w * 0.18f
                
                // Glow circle layer
                drawArc(
                    color = glowColor,
                    startAngle = -90f,
                    sweepAngle = 360f * t,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeGlow, cap = StrokeCap.Round)
                )

                // Core neon circle layer
                val strokeCore = w * 0.08f
                drawArc(
                    color = Color.White,
                    startAngle = -90f,
                    sweepAngle = 360f * t,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeCore, cap = StrokeCap.Round)
                )
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * t,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeCore * 0.6f, cap = StrokeCap.Round)
                )
            }

            "brush" -> {
                val strokeWidth = w * 0.07f
                // Hand drawn organic loop with irregular arc sweep
                drawArc(
                    color = color,
                    startAngle = -100f,
                    sweepAngle = 365f * t,
                    useCenter = false,
                    topLeft = Offset(padding + (w * 0.02f), padding - (w * 0.01f)),
                    size = Size((radius * 2) - (w * 0.02f), (radius * 2) + (w * 0.02f)),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            "pixel" -> {
                val gridBlocks = 7
                val blockSize = (w - padding * 2) / gridBlocks
                
                // Approximate circle blocks
                val blocksToFill = listOf(
                    2 to 0, 3 to 0, 4 to 0,
                    1 to 1, 5 to 1,
                    0 to 2, 6 to 2,
                    0 to 3, 6 to 3,
                    0 to 4, 6 to 4,
                    1 to 5, 5 to 5,
                    2 to 6, 3 to 6, 4 to 6
                )

                val countToDraw = (blocksToFill.size * t).toInt()
                
                for (idx in 0 until countToDraw) {
                    val (col, row) = blocksToFill[idx]
                    val drawX = padding + col * blockSize
                    val drawY = padding + row * blockSize
                    
                    if (isUltra) {
                        // 3D block shadow (high-fidelity depth)
                        drawRect(
                            color = Color.Black.copy(alpha = 0.25f),
                            topLeft = Offset(drawX + blockSize * 0.1f, drawY + blockSize * 0.1f),
                            size = Size(blockSize * 0.9f, blockSize * 0.9f)
                        )
                    }

                    // Main voxel body
                    drawRect(
                        color = color,
                        topLeft = Offset(drawX, drawY),
                        size = Size(blockSize * 0.9f, blockSize * 0.9f)
                    )

                    if (isUltra) {
                        // High-fidelity shiny bevel highlight on each voxel corner
                        drawRect(
                            color = Color.White.copy(alpha = 0.55f),
                            topLeft = Offset(drawX + blockSize * 0.1f, drawY + blockSize * 0.1f),
                            size = Size(blockSize * 0.25f, blockSize * 0.25f)
                        )
                    }
                }
            }

            else -> {
                val strokeWidth = w * 0.08f
                
                if (isUltra) {
                    // 1. Drop shadow arc (3D depth layer)
                    val shadowOffset = w * 0.03f
                    drawArc(
                        color = Color.Black.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f * t,
                        useCenter = false,
                        topLeft = Offset(padding + shadowOffset, padding + shadowOffset),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // 2. Base arc
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * t,
                    useCenter = false,
                    topLeft = Offset(padding, padding),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                if (isUltra) {
                    // 3. Shiny specular reflection highlight arc
                    drawArc(
                        color = Color.White.copy(alpha = 0.5f),
                        startAngle = -90f,
                        sweepAngle = 360f * t,
                        useCenter = false,
                        topLeft = Offset(padding, padding),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth * 0.25f, cap = StrokeCap.Round)
                    )
                }
            }
        }
    }
}
