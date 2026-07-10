package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.GameAchievement
import com.example.data.local.GameSettings
import com.example.data.local.GameStatistics
import com.example.domain.ai.MinimaxAi
import com.example.domain.ai.GeminiAiService
import com.example.repository.GameRepository
import com.example.ui.components.SoundSynthesizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class GameState {
    object Idle : GameState()
    object Playing : GameState()
    data class Won(val winner: String, val winType: String, val winIndices: List<Int>) : GameState()
    object Draw : GameState()
}

data class SessionScore(
    val playerXWins: Int = 0,
    val playerOWins: Int = 0,
    val aiWins: Int = 0,
    val draws: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = GameRepository(database)

    // Game Board State: list of 9 elements (null, "X", "O")
    private val _board = MutableStateFlow<List<String?>>(List(9) { null })
    val board: StateFlow<List<String?>> = _board.asStateFlow()

    // Current turn: "X" or "O"
    private val _currentTurn = MutableStateFlow("X")
    val currentTurn: StateFlow<String> = _currentTurn.asStateFlow()

    // Game State
    private val _gameState = MutableStateFlow<GameState>(GameState.Idle)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // Active Game Mode: "pvp" or "ai"
    private val _gameMode = MutableStateFlow("pvp")
    val gameMode: StateFlow<String> = _gameMode.asStateFlow()

    // AI is calculating
    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // AI reasoning and thoughts
    private val _aiThought = MutableStateFlow<String?>(null)
    val aiThought: StateFlow<String?> = _aiThought.asStateFlow()

    // Session Scoreboard
    private val _sessionScore = MutableStateFlow(SessionScore())
    val sessionScore: StateFlow<SessionScore> = _sessionScore.asStateFlow()

    // Database backed states
    val settings: StateFlow<GameSettings> = repository.settings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameSettings()
    )

    val statistics: StateFlow<GameStatistics> = repository.statistics.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GameStatistics()
    )

    val achievements: StateFlow<List<GameAchievement>> = repository.achievements.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    // Match metadata
    private var matchStartTime: Long = 0L
    private var movesCountInMatch: Int = 0
    private var playerSymbolInMatch: String = "X" // default player as X in AI mode

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        viewModelScope.launch {
            repository.initializeDatabase()
            
            // Apply settings to synthesizer on load
            settings.collect { currentSettings ->
                SoundSynthesizer.isSoundEnabled = currentSettings.soundEnabled
                SoundSynthesizer.isMusicEnabled = currentSettings.musicEnabled
                
                // Toggle background music loop
                if (currentSettings.musicEnabled) {
                    SoundSynthesizer.startMusic()
                } else {
                    SoundSynthesizer.stopMusic()
                }
            }
        }
    }

    fun selectGameMode(mode: String) {
        _gameMode.value = mode
        viewModelScope.launch {
            val currentSettings = settings.value
            repository.saveSettings(currentSettings.copy(lastSelectedMode = mode))
        }
        startNewMatch()
    }

    fun startNewMatch() {
        _board.value = List(9) { null }
        _currentTurn.value = "X"
        _gameState.value = GameState.Playing
        _isAiThinking.value = false
        _aiThought.value = null
        matchStartTime = System.currentTimeMillis()
        movesCountInMatch = 0
        
        // In AI mode, we can randomly decide if human plays X or O,
        // but typically human plays X and starts first. Let's stick to Human = X, AI = O
        playerSymbolInMatch = "X"
    }

    fun playAgain() {
        startNewMatch()
    }

    fun makeMove(index: Int) {
        if (_gameState.value != GameState.Playing || _board.value[index] != null || _isAiThinking.value) return

        val symbol = _currentTurn.value
        val updatedBoard = _board.value.toMutableList()
        updatedBoard[index] = symbol
        _board.value = updatedBoard
        movesCountInMatch++

        triggerHapticFeedback()
        SoundSynthesizer.playPlacement()

        checkBoardOutcome(updatedBoard)

        if (_gameState.value == GameState.Playing) {
            // Switch turn
            val nextTurn = if (symbol == "X") "O" else "X"
            _currentTurn.value = nextTurn

            // If in AI mode and it is AI's turn, trigger AI move
            if (_gameMode.value == "ai" && nextTurn == "O") {
                triggerAiMove()
            }
        }
    }

    private fun triggerAiMove() {
        _isAiThinking.value = true
        _aiThought.value = "AI is thinking..."
        viewModelScope.launch {
            val currentBoard = _board.value
            var selectedMove = -1
            var reason: String? = null

            if (settings.value.useGeminiAi) {
                // Call low-latency Gemini AI API
                val geminiResponse = GeminiAiService.getBestMove(currentBoard)
                if (geminiResponse != null) {
                    selectedMove = geminiResponse.move
                    reason = geminiResponse.reasoning
                } else {
                    // Fallback to local minimax if network fails
                    reason = "Offline fallback"
                    selectedMove = MinimaxAi.findBestMove(
                        board = currentBoard,
                        aiSymbol = "O",
                        opponentSymbol = "X"
                    )
                }
            } else {
                // Standard local Minimax (with artificial delay)
                val delayMs = Random.nextLong(200, 500)
                delay(delayMs)
                reason = "Offline Minimax"
                selectedMove = MinimaxAi.findBestMove(
                    board = currentBoard,
                    aiSymbol = "O",
                    opponentSymbol = "X"
                )
            }

            _aiThought.value = reason

            if (selectedMove != -1 && _gameState.value == GameState.Playing) {
                val updatedBoard = currentBoard.toMutableList()
                updatedBoard[selectedMove] = "O"
                _board.value = updatedBoard
                movesCountInMatch++

                triggerHapticFeedback()
                SoundSynthesizer.playPlacement()

                checkBoardOutcome(updatedBoard)

                if (_gameState.value == GameState.Playing) {
                    _currentTurn.value = "X"
                }
            }
            _isAiThinking.value = false
        }
    }

    private fun checkBoardOutcome(board: List<String?>) {
        val (winnerSymbol, winType, winIndices) = MinimaxAi.checkWinner(board)
        
        if (winnerSymbol != null) {
            _gameState.value = GameState.Won(winnerSymbol, winType, winIndices)
            recordSessionWinner(winnerSymbol)
            
            // Record in Local DB
            val matchDuration = (System.currentTimeMillis() - matchStartTime) / 1000
            viewModelScope.launch {
                repository.recordMatchResult(
                    isAiMode = (_gameMode.value == "ai"),
                    winnerSymbol = winnerSymbol,
                    playerSymbol = playerSymbolInMatch,
                    durationSeconds = matchDuration,
                    movesCount = movesCountInMatch
                )
            }

            // Play win/defeat sound
            if (_gameMode.value == "ai") {
                if (winnerSymbol == playerSymbolInMatch) {
                    SoundSynthesizer.playVictory()
                } else {
                    SoundSynthesizer.playDefeat()
                }
            } else {
                SoundSynthesizer.playVictory()
            }

        } else if (!board.contains(null)) {
            // It's a draw
            _gameState.value = GameState.Draw
            recordSessionWinner(null)

            val matchDuration = (System.currentTimeMillis() - matchStartTime) / 1000
            viewModelScope.launch {
                repository.recordMatchResult(
                    isAiMode = (_gameMode.value == "ai"),
                    winnerSymbol = null,
                    playerSymbol = playerSymbolInMatch,
                    durationSeconds = matchDuration,
                    movesCount = movesCountInMatch
                )
            }
            SoundSynthesizer.playDraw()
        }
    }

    private fun recordSessionWinner(winner: String?) {
        val currentScore = _sessionScore.value
        if (winner == null) {
            _sessionScore.value = currentScore.copy(draws = currentScore.draws + 1)
        } else if (_gameMode.value == "ai") {
            if (winner == "O") {
                _sessionScore.value = currentScore.copy(aiWins = currentScore.aiWins + 1)
            } else {
                _sessionScore.value = currentScore.copy(playerXWins = currentScore.playerXWins + 1)
            }
        } else {
            if (winner == "X") {
                _sessionScore.value = currentScore.copy(playerXWins = currentScore.playerXWins + 1)
            } else {
                _sessionScore.value = currentScore.copy(playerOWins = currentScore.playerOWins + 1)
            }
        }
    }

    fun resetSessionScore() {
        _sessionScore.value = SessionScore()
        SoundSynthesizer.playClick()
    }

    // Settings adjustments
    fun updateSoundSetting(enabled: Boolean) {
        viewModelScope.launch {
            val updated = settings.value.copy(soundEnabled = enabled)
            repository.saveSettings(updated)
            SoundSynthesizer.isSoundEnabled = enabled
            SoundSynthesizer.playToggle()
        }
    }

    fun updateMusicSetting(enabled: Boolean) {
        viewModelScope.launch {
            val updated = settings.value.copy(musicEnabled = enabled)
            repository.saveSettings(updated)
            SoundSynthesizer.isMusicEnabled = enabled
            if (enabled) {
                SoundSynthesizer.startMusic()
            } else {
                SoundSynthesizer.stopMusic()
            }
            SoundSynthesizer.playToggle()
        }
    }

    fun updateVibrationSetting(enabled: Boolean) {
        viewModelScope.launch {
            val updated = settings.value.copy(vibrationEnabled = enabled)
            repository.saveSettings(updated)
            SoundSynthesizer.playToggle()
        }
    }

    fun updateDarkModeSetting(mode: String) {
        viewModelScope.launch {
            val updated = settings.value.copy(darkMode = mode)
            repository.saveSettings(updated)
            SoundSynthesizer.playToggle()
        }
    }

    fun updateBoardTheme(themeName: String) {
        viewModelScope.launch {
            val updated = settings.value.copy(selectedBoardTheme = themeName)
            repository.saveSettings(updated)
            SoundSynthesizer.playClick()
        }
    }

    fun updateXStyle(styleName: String) {
        viewModelScope.launch {
            val updated = settings.value.copy(selectedXStyle = styleName)
            repository.saveSettings(updated)
            SoundSynthesizer.playClick()
        }
    }

    fun updateOStyle(styleName: String) {
        viewModelScope.launch {
            val updated = settings.value.copy(selectedOStyle = styleName)
            repository.saveSettings(updated)
            SoundSynthesizer.playClick()
        }
    }

    fun updateVictoryEffect(effectName: String) {
        viewModelScope.launch {
            val updated = settings.value.copy(selectedVictoryEffect = effectName)
            repository.saveSettings(updated)
            SoundSynthesizer.playClick()
        }
    }

    fun updateButtonStyle(styleName: String) {
        viewModelScope.launch {
            val updated = settings.value.copy(selectedButtonStyle = styleName)
            repository.saveSettings(updated)
            SoundSynthesizer.playClick()
        }
    }

    fun updateUltraResolutionSetting(enabled: Boolean) {
        viewModelScope.launch {
            val updated = settings.value.copy(ultraResolution = enabled)
            repository.saveSettings(updated)
            SoundSynthesizer.playToggle()
        }
    }

    fun updateUseGeminiAiSetting(enabled: Boolean) {
        viewModelScope.launch {
            val updated = settings.value.copy(useGeminiAi = enabled)
            repository.saveSettings(updated)
            SoundSynthesizer.playToggle()
        }
    }

    fun resetAllStatisticsAndAchievements() {
        viewModelScope.launch {
            repository.resetStatistics()
            resetSessionScore()
            SoundSynthesizer.playToggle()
        }
    }

    fun triggerHapticFeedback() {
        if (settings.value.vibrationEnabled) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(30)
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        SoundSynthesizer.stopMusic()
    }
}
