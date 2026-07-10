package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontFamily
import com.example.data.local.GameSettings
import com.example.ui.viewmodel.SessionScore
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.domain.model.ThemeColors
import com.example.ui.components.AppBackground
import com.example.ui.components.ConfettiEffect
import com.example.ui.components.DrawO
import com.example.ui.components.DrawX
import com.example.ui.viewmodel.GameState
import com.example.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    mode: String,
    onNavigateBack: () -> Unit
) {
    val board by viewModel.board.collectAsState()
    val currentTurn by viewModel.currentTurn.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()
    val aiThought by viewModel.aiThought.collectAsState()
    val sessionScore by viewModel.sessionScore.collectAsState()
    val settingsState by viewModel.settings.collectAsState()
    
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)
    
    // Auto-init game if idle
    LaunchedEffect(key1 = mode) {
        viewModel.selectGameMode(mode)
    }

    // Responsive sizing constraints
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val boardSize = minOf(screenWidth - 48.dp, 360.dp)

    AppBackground(
        themeColors = themeColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Back, Title, Reset Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        viewModel.triggerHapticFeedback()
                        onNavigateBack()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = themeColors.primaryText
                    )
                }

                Text(
                    text = if (mode == "ai") "PLAYER VS AI" else "PLAYER VS PLAYER",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText,
                    letterSpacing = 1.sp
                )

                IconButton(
                    onClick = {
                        viewModel.triggerHapticFeedback()
                        viewModel.resetSessionScore()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Score",
                        tint = themeColors.primaryText.copy(alpha = 0.7f)
                    )
                }
            }

            // Scoreboard Row
            Scoreboard(
                mode = mode,
                sessionScore = sessionScore,
                themeColors = themeColors
            )

            // Turn Indicator Banner
            TurnIndicator(
                currentTurn = currentTurn,
                isAiThinking = isAiThinking,
                gameState = gameState,
                themeColors = themeColors,
                settingsState = settingsState
            )

            // Gemini AI Reasoning Thought Bubble
            if (mode == "ai" && aiThought != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, themeColors.boardLine.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Thought",
                            tint = themeColors.accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = aiThought ?: "",
                            fontSize = 13.sp,
                            color = themeColors.primaryText.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // 3x3 Game Board Container
            if (themeColors.isGlass) {
                Box(
                    modifier = Modifier
                        .size(boardSize)
                        .clip(RoundedCornerShape(32.dp))
                        .background(themeColors.surface)
                        .border(
                            BorderStroke(1.dp, themeColors.boardLine),
                            shape = RoundedCornerShape(32.dp)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (row in 0..2) {
                            Row(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (col in 0..2) {
                                    val idx = row * 3 + col
                                    val cellSymbol = board[idx]

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (themeColors.isDark) Color(0x3BFFFFFF) else Color(0x94FFFFFF)
                                            )
                                            .clickable {
                                                if (cellSymbol == null && gameState is GameState.Playing && !isAiThinking) {
                                                    viewModel.makeMove(idx)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cellSymbol != null) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize(0.7f)
                                                    .padding(6.dp)
                                            ) {
                                                if (cellSymbol == "X") {
                                                    DrawX(
                                                        color = themeColors.xColor,
                                                        style = settingsState.selectedXStyle,
                                                        isUltra = settingsState.ultraResolution
                                                    )
                                                } else {
                                                    DrawO(
                                                        color = themeColors.oColor,
                                                        style = settingsState.selectedOStyle,
                                                        isUltra = settingsState.ultraResolution
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Dynamic Winning Line overlay
                    if (gameState is GameState.Won) {
                        val wonState = gameState as GameState.Won
                        WinningLineOverlay(
                            winIndices = wonState.winIndices,
                            lineColor = if (wonState.winner == "X") themeColors.xColor else themeColors.oColor
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(boardSize)
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeColors.surface.copy(alpha = 0.5f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Background board lines
                    GameBoardLines(themeColors = themeColors)

                    // 3x3 Grid Cells
                    Column(modifier = Modifier.fillMaxSize()) {
                        for (row in 0..2) {
                            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                for (col in 0..2) {
                                    val idx = row * 3 + col
                                    val cellSymbol = board[idx]

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight()
                                            .clickable {
                                                if (cellSymbol == null && gameState is GameState.Playing && !isAiThinking) {
                                                    viewModel.makeMove(idx)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (cellSymbol != null) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize(0.75f)
                                                    .padding(8.dp)
                                            ) {
                                                if (cellSymbol == "X") {
                                                    DrawX(
                                                        color = themeColors.xColor,
                                                        style = settingsState.selectedXStyle,
                                                        isUltra = settingsState.ultraResolution
                                                    )
                                                } else {
                                                    DrawO(
                                                        color = themeColors.oColor,
                                                        style = settingsState.selectedOStyle,
                                                        isUltra = settingsState.ultraResolution
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Dynamic Winning Line overlay
                    if (gameState is GameState.Won) {
                        val wonState = gameState as GameState.Won
                        WinningLineOverlay(
                            winIndices = wonState.winIndices,
                            lineColor = if (wonState.winner == "X") themeColors.xColor else themeColors.oColor
                        )
                    }
                }
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.triggerHapticFeedback()
                        viewModel.startNewMatch()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.xColor),
                    shape = getCustomButtonShape(settingsState.selectedButtonStyle)
                ) {
                    Text(
                        text = "Restart",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Button(
                    onClick = {
                        viewModel.triggerHapticFeedback()
                        viewModel.startNewMatch()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                    shape = getCustomButtonShape(settingsState.selectedButtonStyle)
                ) {
                    Text(
                        text = "New Match",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Overlay: End Game Modal Dialogue
        AnimatedVisibility(
            visible = gameState is GameState.Won || gameState is GameState.Draw,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            EndGameDialog(
                gameState = gameState,
                mode = mode,
                themeColors = themeColors,
                settingsState = settingsState,
                onPlayAgain = {
                    viewModel.triggerHapticFeedback()
                    viewModel.playAgain()
                },
                onReset = {
                    viewModel.triggerHapticFeedback()
                    viewModel.startNewMatch()
                }
            )
        }

        // Confetti Win Celebration Layer
        if (gameState is GameState.Won && settingsState.selectedVictoryEffect != "none") {
            val wonState = gameState as GameState.Won
            // Only shower confetti if human wins or if in PvP mode
            val shouldShowConfetti = if (mode == "ai") {
                wonState.winner == "X" // Human is X
            } else {
                true
            }
            if (shouldShowConfetti) {
                ConfettiEffect()
            }
        }
    }
}

@Composable
fun getCustomButtonShape(style: String): RoundedCornerShape {
    return when (style.lowercase()) {
        "sharp" -> RoundedCornerShape(0.dp)
        "floating" -> RoundedCornerShape(18.dp)
        else -> RoundedCornerShape(50.dp) // rounded
    }
}

@Composable
fun Scoreboard(
    mode: String,
    sessionScore: SessionScore,
    themeColors: ThemeColors
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(themeColors.surface.copy(alpha = 0.4f))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PLAYER X",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.xColor,
                letterSpacing = 0.5.sp
            )
            Text(
                text = sessionScore.playerXWins.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColors.primaryText
            )
        }

        Divider(
            modifier = Modifier
                .height(30.dp)
                .width(1.dp),
            color = themeColors.boardLine.copy(alpha = 0.3f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "DRAWS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.primaryText.copy(alpha = 0.6f),
                letterSpacing = 0.5.sp
            )
            Text(
                text = sessionScore.draws.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColors.primaryText
            )
        }

        Divider(
            modifier = Modifier
                .height(30.dp)
                .width(1.dp),
            color = themeColors.boardLine.copy(alpha = 0.3f)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (mode == "ai") "PERFECT AI" else "PLAYER O",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.oColor,
                letterSpacing = 0.5.sp
            )
            Text(
                text = if (mode == "ai") sessionScore.aiWins.toString() else sessionScore.playerOWins.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = themeColors.primaryText
            )
        }
    }
}

@Composable
fun TurnIndicator(
    currentTurn: String,
    isAiThinking: Boolean,
    gameState: GameState,
    themeColors: ThemeColors,
    settingsState: GameSettings
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(themeColors.surface.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        if (gameState is GameState.Playing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (isAiThinking) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "AI thinking",
                        tint = themeColors.oColor,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 6.dp)
                    )
                    Text(
                        text = "AI is thinking perfect move...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.oColor,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    Text(
                        text = "TURN: ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = themeColors.primaryText.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(18.dp)) {
                        if (currentTurn == "X") {
                            DrawX(color = themeColors.xColor, style = settingsState.selectedXStyle)
                        } else {
                            DrawO(color = themeColors.oColor, style = settingsState.selectedOStyle)
                        }
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${currentTurn})",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentTurn == "X") themeColors.xColor else themeColors.oColor
                    )
                }
            }
        } else {
            Text(
                text = "MATCH COMPLETED",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = themeColors.accent,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun GameBoardLines(themeColors: ThemeColors) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cellW = w / 3f
        val cellH = h / 3f
        val lineStroke = w * 0.02f

        // Verticals
        drawLine(
            color = themeColors.boardLine,
            start = Offset(cellW, 16f),
            end = Offset(cellW, h - 16f),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = themeColors.boardLine,
            start = Offset(cellW * 2, 16f),
            end = Offset(cellW * 2, h - 16f),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round
        )

        // Horizontals
        drawLine(
            color = themeColors.boardLine,
            start = Offset(16f, cellH),
            end = Offset(w - 16f, cellH),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = themeColors.boardLine,
            start = Offset(16f, cellH * 2),
            end = Offset(w - 16f, cellH * 2),
            strokeWidth = lineStroke,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun WinningLineOverlay(
    winIndices: List<Int>,
    lineColor: Color
) {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(winIndices) {
        progress.animateTo(1f, animationSpec = tween(400, easing = LinearOutSlowInEasing))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (winIndices.size < 3) return@Canvas
        
        val w = size.width
        val h = size.height
        val cellW = w / 3f
        val cellH = h / 3f

        fun getCenter(idx: Int): Offset {
            val row = idx / 3
            val col = idx % 3
            return Offset(col * cellW + cellW / 2f, row * cellH + cellH / 2f)
        }

        val start = getCenter(winIndices[0])
        val end = getCenter(winIndices[2])

        val animatedEnd = Offset(
            start.x + (end.x - start.x) * progress.value,
            start.y + (end.y - start.y) * progress.value
        )

        drawLine(
            color = lineColor,
            start = start,
            end = animatedEnd,
            strokeWidth = w * 0.035f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun EndGameDialog(
    gameState: GameState,
    mode: String,
    themeColors: ThemeColors,
    settingsState: GameSettings,
    onPlayAgain: () -> Unit,
    onReset: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = themeColors.surface),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .border(2.dp, themeColors.boardLine.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val title: String
                val desc: String
                val primaryColor: Color
                var winnerSym: String? = null

                when (gameState) {
                    is GameState.Won -> {
                        winnerSym = gameState.winner
                        primaryColor = if (winnerSym == "X") themeColors.xColor else themeColors.oColor
                        if (mode == "ai") {
                            if (winnerSym == "X") {
                                title = "VICTORY!"
                                desc = "Unbelievable! You defeated the Perfect AI."
                            } else {
                                title = "DEFEAT"
                                desc = "The Minimax AI computed a perfect victory."
                            }
                        } else {
                            title = "MATCH WON!"
                            desc = "Player $winnerSym has claimed total victory!"
                        }
                    }
                    else -> {
                        title = "MATCH DRAW"
                        desc = "The board is full. Perfect defensive moves."
                        primaryColor = themeColors.accent
                    }
                }

                // Victory/Defeat icon or symbol
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(50))
                        .background(primaryColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (winnerSym != null) {
                        Box(modifier = Modifier.size(50.dp)) {
                            if (winnerSym == "X") {
                                DrawX(color = primaryColor, style = settingsState.selectedXStyle)
                            } else {
                                DrawO(color = primaryColor, style = settingsState.selectedOStyle)
                            }
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Draw",
                            tint = primaryColor,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }

                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = primaryColor,
                    textAlign = TextAlign.Center,
                    letterSpacing = 1.sp
                )

                Text(
                    text = desc,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = themeColors.primaryText.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onPlayAgain,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = getCustomButtonShape(settingsState.selectedButtonStyle)
                ) {
                    Text(
                        text = "Play Again",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColors.primaryText),
                    border = BorderStroke(1.5.dp, themeColors.boardLine.copy(alpha = 0.4f)),
                    shape = getCustomButtonShape(settingsState.selectedButtonStyle)
                ) {
                    Text(
                        text = "New Match",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
