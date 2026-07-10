package com.example.domain.model

import androidx.compose.ui.graphics.Color

data class ThemeColors(
    val background: Color,
    val surface: Color,
    val boardLine: Color,
    val xColor: Color,
    val oColor: Color,
    val primaryText: Color,
    val accent: Color,
    val isDark: Boolean,
    val isGlass: Boolean = false
)

object GameThemes {
    val Classic = ThemeColors(
        background = Color(0xFF1E293B), // slate-800
        surface = Color(0xFF334155),    // slate-700
        boardLine = Color(0xFF475569),   // slate-600
        xColor = Color(0xFF38BDF8),      // sky-400
        oColor = Color(0xFFF43F5E),      // rose-500
        primaryText = Color(0xFFF8FAFC), // slate-50
        accent = Color(0xFF10B981),      // emerald-500
        isDark = true,
        isGlass = false
    )

    val Neon = ThemeColors(
        background = Color(0xFF0F0B1E), // Deep space
        surface = Color(0xFF1E1530),    // Deep violet
        boardLine = Color(0xFFBC13FE),   // Neon magenta
        xColor = Color(0xFF39FF14),      // Neon green
        oColor = Color(0xFFFF073A),      // Neon pink
        primaryText = Color(0xFFFFFFFF),
        accent = Color(0xFF00FFFF),      // Cyan
        isDark = true,
        isGlass = false
    )

    val Cyberpunk = ThemeColors(
        background = Color(0xFF0D0D0D), // absolute black
        surface = Color(0xFF1C1C1C),    // dark gray
        boardLine = Color(0xFFFFB300),   // amber gold
        xColor = Color(0xFFFF0055),      // neon red
        oColor = Color(0xFF00FFCC),      // neon teal
        primaryText = Color(0xFFECEFF1),
        accent = Color(0xFFFFB300),
        isDark = true,
        isGlass = false
    )

    val Forest = ThemeColors(
        background = Color(0xFF1B2A24), // deep forest
        surface = Color(0xFF2A3C35),    // soft moss
        boardLine = Color(0xFF4E6B5F),   // sage green
        xColor = Color(0xFFE2EAF4),      // off-white
        oColor = Color(0xFFF5E6C4),      // warm beige
        primaryText = Color(0xFFFFFFFF),
        accent = Color(0xFF81C784),      // pastel green
        isDark = true,
        isGlass = false
    )

    val MinimalWood = ThemeColors(
        background = Color(0xFFFDFBF7), // cream ivory
        surface = Color(0xFFF3EFE9),    // warm gray
        boardLine = Color(0xFF4A3E3D),   // deep brown
        xColor = Color(0xFF4A3E3D),      // deep brown
        oColor = Color(0xFFC68A4C),      // soft wood orange
        primaryText = Color(0xFF2C2423), // dark charcoal
        accent = Color(0xFF8B5A2B),      // bronze
        isDark = false,
        isGlass = false
    )

    val FrostedGlass = ThemeColors(
        background = Color(0xFFE9EFFF), // soft light blue-gray
        surface = Color(0x66FFFFFF),    // semi-translucent white bg-white/40
        boardLine = Color(0x99FFFFFF),   // translucent white border
        xColor = Color(0xFF3B82F6),      // blue-500
        oColor = Color(0xFFF43F5E),      // rose-500
        primaryText = Color(0xFF1E293B), // slate-800
        accent = Color(0xFF2563EB),      // blue-600
        isDark = false,
        isGlass = true
    )

    val DarkFrostedGlass = ThemeColors(
        background = Color(0xFF0B132B), // deep cosmic slate/navy
        surface = Color(0x2BFFFFFF),    // deep glass bg-white/17
        boardLine = Color(0x40FFFFFF),   // border white/25
        xColor = Color(0xFF38BDF8),      // sky-400
        oColor = Color(0xFFF43F5E),      // rose-500
        primaryText = Color(0xFFF8FAFC), // slate-50
        accent = Color(0xFF10B981),      // emerald-500
        isDark = true,
        isGlass = true
    )

    fun getTheme(themeName: String, isSystemDark: Boolean = true): ThemeColors {
        return when (themeName.lowercase()) {
            "neon" -> Neon
            "cyberpunk" -> Cyberpunk
            "forest" -> Forest
            "minimal" -> MinimalWood
            "glass" -> if (isSystemDark) DarkFrostedGlass else FrostedGlass
            else -> {
                // Classic slate is default, but we can also adapt to light/dark system toggle
                if (!isSystemDark) {
                    // Light alternative of slate theme
                    ThemeColors(
                        background = Color(0xFFF1F5F9), // slate-100
                        surface = Color(0xFFE2E8F0),    // slate-200
                        boardLine = Color(0xFF94A3B8),   // slate-400
                        xColor = Color(0xFF0284C7),      // sky-600
                        oColor = Color(0xFFE11D48),      // rose-600
                        primaryText = Color(0xFF0F172A), // slate-900
                        accent = Color(0xFF059669),      // emerald-600
                        isDark = false,
                        isGlass = false
                    )
                } else {
                    Classic
                }
            }
        }
    }
}
