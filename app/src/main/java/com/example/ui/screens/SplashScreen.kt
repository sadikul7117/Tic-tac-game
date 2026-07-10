package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.DrawO
import com.example.ui.components.DrawX
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    selectedTheme: String,
    onNavigateToHome: () -> Unit
) {
    val themeColors = GameThemes.getTheme(selectedTheme, isSystemDark = true)
    
    // Animations
    val transitionState = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) {
        transitionState.targetState = true
        delay(1800)
        onNavigateToHome()
    }

    val transition = rememberTransition(transitionState, label = "splashTransition")
    
    val alphaAnim by transition.animateFloat(
        transitionSpec = { tween(1000, easing = LinearOutSlowInEasing) },
        label = "alpha"
    ) { state -> if (state) 1f else 0f }

    val scaleAnim by transition.animateFloat(
        transitionSpec = { spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow) },
        label = "scale"
    ) { state -> if (state) 1f else 0.4f }

    // Floating symbol spin
    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        themeColors.background,
                        themeColors.surface
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .scale(scaleAnim)
                .alpha(alphaAnim)
        ) {
            // Visual Logo Header: Custom animated X and O side-by-side
            Row(
                modifier = Modifier
                    .size(140.dp)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(1.1f)
                ) {
                    DrawX(color = themeColors.xColor, style = "classic")
                }
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .scale(1.1f)
                ) {
                    DrawO(color = themeColors.oColor, style = "classic")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Title
            Text(
                text = "TIC-TAC-TOE",
                fontSize = 38.sp,
                color = themeColors.primaryText,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 4.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Subtitle
            Text(
                text = "PREMIUM OFFLINE MATCHES",
                fontSize = 11.sp,
                color = themeColors.xColor,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .alpha(0.8f)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Loading spinner matching theme accent
            CircularProgressIndicator(
                color = themeColors.accent,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
