package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.GameThemes
import com.example.ui.components.AppBackground
import com.example.ui.components.DrawO
import com.example.ui.components.DrawX
import com.example.ui.viewmodel.GameViewModel

@Composable
fun CustomizationScreen(
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit
) {
    val settingsState by viewModel.settings.collectAsState()
    val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    val themeColors = GameThemes.getTheme(settingsState.selectedBoardTheme, isSystemDark = isDark)
    
    AppBackground(
        themeColors = themeColors
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
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
                    text = "CUSTOMIZE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primaryText,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Scrollable Settings
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // LIVE VISUAL PREVIEW CARD
                Text(
                    text = "LIVE THEME PREVIEW",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.xColor,
                    letterSpacing = 1.sp
                )

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = themeColors.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp, 
                            color = themeColors.boardLine.copy(alpha = 0.3f), 
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Tiny interactive sample grid showing customizable parts
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(themeColors.background.copy(alpha = 0.5f))
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.size(50.dp)) {
                                DrawX(
                                    color = themeColors.xColor,
                                    style = settingsState.selectedXStyle,
                                    isUltra = settingsState.ultraResolution
                                )
                            }
                            Divider(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(2.dp),
                                color = themeColors.boardLine
                            )
                            Box(modifier = Modifier.size(50.dp)) {
                                DrawO(
                                    color = themeColors.oColor,
                                    style = settingsState.selectedOStyle,
                                    isUltra = settingsState.ultraResolution
                                )
                            }
                        }

                        // Preview of Button Styles
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = themeColors.accent),
                            shape = getCustomButtonShape(settingsState.selectedButtonStyle),
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            Text(
                                text = "Sample Button", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // 1. BOARD THEMES SELECTION
                CustomizationSectionHeader(title = "BOARD SCHEMES", icon = Icons.Default.Palette, themeColors = themeColors)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val themes = listOf(
                        "glass" to "Frosted Glass",
                        "classic" to "Classic Slate",
                        "neon" to "Neon Laser",
                        "cyberpunk" to "Cyber Amber",
                        "forest" to "Emerald Forest",
                        "minimal" to "Minimal Wood"
                    )
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        themes.chunked(3).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { (id, name) ->
                                    val isSelected = settingsState.selectedBoardTheme == id
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) themeColors.accent else themeColors.surface
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.updateBoardTheme(id) }
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else themeColors.primaryText,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp, horizontal = 4.dp)
                                        )
                                    }
                                }
                                if (rowItems.size < 3) {
                                    Spacer(modifier = Modifier.weight((3 - rowItems.size).toFloat()))
                                }
                            }
                        }
                    }
                }

                // 2. PIECE STYLE - X
                CustomizationSectionHeader(title = "SYMBOL X DESIGN", icon = Icons.Default.Palette, themeColors = themeColors)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val xStyles = listOf("classic" to "Lines", "neon" to "Glow", "brush" to "Brush", "pixel" to "Pixel")
                    xStyles.forEach { (id, name) ->
                        val isSelected = settingsState.selectedXStyle == id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) themeColors.accent else themeColors.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateXStyle(id) }
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else themeColors.primaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                // 3. PIECE STYLE - O
                CustomizationSectionHeader(title = "SYMBOL O DESIGN", icon = Icons.Default.Palette, themeColors = themeColors)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val oStyles = listOf("classic" to "Circle", "neon" to "Glow", "brush" to "Brush", "pixel" to "Pixel")
                    oStyles.forEach { (id, name) ->
                        val isSelected = settingsState.selectedOStyle == id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) themeColors.accent else themeColors.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateOStyle(id) }
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else themeColors.primaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                // 4. BUTTON STYLE DESIGN
                CustomizationSectionHeader(title = "BUTTONS STYLE", icon = Icons.Default.Palette, themeColors = themeColors)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val bStyles = listOf("rounded" to "Rounded", "sharp" to "Sharp", "floating" to "Floating")
                    bStyles.forEach { (id, name) ->
                        val isSelected = settingsState.selectedButtonStyle == id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) themeColors.accent else themeColors.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateButtonStyle(id) }
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else themeColors.primaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                // 5. VICTORY EFFECT CELEBRATIONS
                CustomizationSectionHeader(title = "CELEBRATION EFFECT", icon = Icons.Default.Palette, themeColors = themeColors)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val effects = listOf("confetti" to "Confetti Rain", "none" to "No Effects")
                    effects.forEach { (id, name) ->
                        val isSelected = settingsState.selectedVictoryEffect == id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) themeColors.accent else themeColors.surface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.updateVictoryEffect(id) }
                        ) {
                            Text(
                                text = name,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else themeColors.primaryText,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CustomizationSectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    themeColors: com.example.domain.model.ThemeColors
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = themeColors.primaryText.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = themeColors.primaryText.copy(alpha = 0.6f),
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 1.5.sp
        )
    }
}
