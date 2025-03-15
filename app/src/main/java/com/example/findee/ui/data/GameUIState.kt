package com.example.findee.ui.data

data class GameUIState(
    val currentScrambledWord: String = "",
    val isGuessedWordWrong: Boolean = false,
    var score: Int = 0,
    var currentWordCount: Int = 1,
    var isGameOver:Boolean = false
)
