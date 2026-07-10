package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val gameViewModel: GameViewModel = viewModel()
      val settingsState by gameViewModel.settings.collectAsState()

      // Dynamically toggle light/dark theme depending on settings selection
      val isDark = when (settingsState.darkMode.lowercase()) {
        "light" -> false
        "dark" -> true
        else -> true // default to elegant dark mode
      }

      MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
        Surface(
          modifier = Modifier.fillMaxSize()
        ) {
          val navController = rememberNavController()

          NavHost(
            navController = navController,
            startDestination = "splash"
          ) {
            composable("splash") {
              SplashScreen(
                selectedTheme = settingsState.selectedBoardTheme,
                onNavigateToHome = {
                  navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                  }
                }
              )
            }

            composable("home") {
              HomeScreen(
                viewModel = gameViewModel,
                onNavigateToGame = { mode ->
                  navController.navigate("game/$mode")
                },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToStats = { navController.navigate("statistics") },
                onNavigateToAchievements = { navController.navigate("achievements") },
                onNavigateToCustomization = { navController.navigate("customization") },
                onNavigateToAbout = { navController.navigate("about") }
              )
            }

            composable(
              route = "game/{mode}",
              arguments = listOf(navArgument("mode") { type = NavType.StringType })
            ) { backStackEntry ->
              val mode = backStackEntry.arguments?.getString("mode") ?: "pvp"
              GameScreen(
                viewModel = gameViewModel,
                mode = mode,
                onNavigateBack = { navController.popBackStack() }
              )
            }

            composable("settings") {
              SettingsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAbout = { navController.navigate("about") },
                onNavigateToPrivacy = { navController.navigate("privacy") }
              )
            }

            composable("statistics") {
              StatisticsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }

            composable("achievements") {
              AchievementsScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }

            composable("customization") {
              CustomizationScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }

            composable("about") {
              AboutScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }

            composable("privacy") {
              PrivacyPolicyScreen(
                viewModel = gameViewModel,
                onNavigateBack = { navController.popBackStack() }
              )
            }
          }
        }
      }
    }
  }
}
