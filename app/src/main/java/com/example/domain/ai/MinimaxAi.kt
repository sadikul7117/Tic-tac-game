package com.example.domain.ai

object MinimaxAi {

    /**
     * Finds the best move on the 3x3 board for the AI.
     * Returns an index from 0 to 8, or -1 if the board is full.
     */
    fun findBestMove(board: List<String?>, aiSymbol: String, opponentSymbol: String): Int {
        var bestVal = -1000
        var bestMove = -1
        
        // Find empty cells
        for (i in 0..8) {
            if (board[i] == null) {
                // Make the move
                val tempBoard = board.toMutableList()
                tempBoard[i] = aiSymbol
                
                // Compute evaluation function for this move
                val moveVal = minimax(tempBoard, 0, -1000, 1000, false, aiSymbol, opponentSymbol)
                
                // If the value of the current move is more than the best value, update best
                if (moveVal > bestVal) {
                    bestMove = i
                    bestVal = moveVal
                }
            }
        }
        return bestMove
    }

    private fun minimax(
        board: MutableList<String?>,
        depth: Int,
        alpha: Int,
        beta: Int,
        isMax: Boolean,
        aiSymbol: String,
        opponentSymbol: String
    ): Int {
        val score = evaluateBoard(board, aiSymbol, opponentSymbol)

        // If Maximizer has won, return score
        if (score == 10) return score - depth

        // If Minimizer has won, return score
        if (score == -10) return score + depth

        // If no moves left (Draw), return 0
        if (!isMovesLeft(board)) return 0

        var currentAlpha = alpha
        var currentBeta = beta

        if (isMax) {
            var best = -1000
            for (i in 0..8) {
                if (board[i] == null) {
                    board[i] = aiSymbol
                    best = maxOf(best, minimax(board, depth + 1, currentAlpha, currentBeta, false, aiSymbol, opponentSymbol))
                    board[i] = null
                    currentAlpha = maxOf(currentAlpha, best)
                    if (currentBeta <= currentAlpha) break
                }
            }
            return best
        } else {
            var best = 1000
            for (i in 0..8) {
                if (board[i] == null) {
                    board[i] = opponentSymbol
                    best = minOf(best, minimax(board, depth + 1, currentAlpha, currentBeta, true, aiSymbol, opponentSymbol))
                    board[i] = null
                    currentBeta = minOf(currentBeta, best)
                    if (currentBeta <= currentAlpha) break
                }
            }
            return best
        }
    }

    private fun isMovesLeft(board: List<String?>): Boolean {
        return board.contains(null)
    }

    private fun evaluateBoard(board: List<String?>, aiSymbol: String, opponentSymbol: String): Int {
        // Checking Rows for X or O victory
        for (row in 0..2) {
            val idx = row * 3
            if (board[idx] != null && board[idx] == board[idx + 1] && board[idx + 1] == board[idx + 2]) {
                if (board[idx] == aiSymbol) return +10
                if (board[idx] == opponentSymbol) return -10
            }
        }

        // Checking Columns for X or O victory
        for (col in 0..2) {
            if (board[col] != null && board[col] == board[col + 3] && board[col + 3] == board[col + 6]) {
                if (board[col] == aiSymbol) return +10
                if (board[col] == opponentSymbol) return -10
            }
        }

        // Checking Diagonals for X or O victory
        if (board[0] != null && board[0] == board[4] && board[4] == board[8]) {
            if (board[0] == aiSymbol) return +10
            if (board[0] == opponentSymbol) return -10
        }

        if (board[2] != null && board[2] == board[4] && board[4] == board[6]) {
            if (board[2] == aiSymbol) return +10
            if (board[2] == opponentSymbol) return -10
        }

        // Else if none has won then return 0
        return 0
    }

    /**
     * Checks if there's a winner on the board.
     * Returns a Triple of (WinnerSymbol?, WinType, WinningIndices)
     * WinType: "row", "col", "diag1", "diag2", or "none"
     */
    fun checkWinner(board: List<String?>): Triple<String?, String, List<Int>> {
        // Rows
        for (row in 0..2) {
            val idx = row * 3
            if (board[idx] != null && board[idx] == board[idx + 1] && board[idx + 1] == board[idx + 2]) {
                return Triple(board[idx], "row", listOf(idx, idx + 1, idx + 2))
            }
        }

        // Columns
        for (col in 0..2) {
            if (board[col] != null && board[col] == board[col + 3] && board[col + 3] == board[col + 6]) {
                return Triple(board[col], "col", listOf(col, col + 3, col + 6))
            }
        }

        // Diagonals
        if (board[0] != null && board[0] == board[4] && board[4] == board[8]) {
            return Triple(board[0], "diag1", listOf(0, 4, 8))
        }

        if (board[2] != null && board[2] == board[4] && board[4] == board[6]) {
            return Triple(board[2], "diag2", listOf(2, 4, 6))
        }

        // Check Draw
        if (!board.contains(null)) {
            return Triple(null, "draw", emptyList())
        }

        return Triple(null, "none", emptyList())
    }
}
