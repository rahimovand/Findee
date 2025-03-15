package com.example.findee.ui.GameViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.findee.ui.data.GameUIState
import com.example.findee.ui.data.MAX_NO_OF_WORDS
import com.example.findee.ui.data.SCORE_INCREASE
import com.example.findee.ui.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<GameUIState> = MutableStateFlow(GameUIState())
    val uiState: StateFlow<GameUIState> = _uiState.asStateFlow()

    private lateinit var currentWord: String
    var userGuess by mutableStateOf("")
    private val usedWords = mutableSetOf<String>()

    init {
        resetGame()
    }

    fun updateUserGuess(guessWord: String) {
        userGuess = guessWord
    }

    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUIState(currentScrambledWord = pickRandomAndShuffle())
    }

    private fun pickRandomAndShuffle(): String {
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleWord(currentWord)
        }
    }

    private fun shuffleWord(word: String): String {
        val tempWord = word.toCharArray()

        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    fun updatedGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS) {
            _uiState.update {  currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )

            }
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomAndShuffle(),
                    score = updatedScore,
                    currentWordCount = currentState.currentWordCount.inc()
                )
            }
        }

    }

    fun skipWord() {
        updatedGameState(
            _uiState.value.score
        )
        updateUserGuess("")
    }

    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updatedGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = true
                )
            }
        }
        updateUserGuess("")
    }

}