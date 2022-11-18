package com.example.android.unscramble.ui.game

import android.text.Spannable
import android.text.SpannableString
import android.text.style.TtsSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 * Bagian ViewModel yang berisi data aplikasi dan metode untuk memproses data.
 */
class GameViewModel : ViewModel(){
    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    private var wordsList: MutableList<String> = mutableListOf() // Untuk menghindari pengulangan kata yang sama.
    private lateinit var currentWord: String // Untuk menyimpan kata yang disusun.

    init {
        getNextWord()
    }

    // Fungsi untuk menampilkan kata berikutnya dengan acak.
    private fun getNextWord() {
        currentWord = allWordsList.random() // Untuk mendapatkan kata acak dari allWordsList.
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle() // Menggunakan shuffle() untuk membuat kata acak.

        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle() // Menggunakan shuffle() untuk membuat kata acak.
        }
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = (_currentWordCount.value)?.inc() // Meningkatkan nilai satu per satu
            wordsList.add(currentWord)
        }
    }

    // Menginisialisasi ulang data game untuk memulai ulang game.
    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

    // Untuk meningkatkan skor permainan jika kata yang dimasukkan benar.
    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    // Mengembalikan nilai true jika jawaban benar dan skor akan meningkat.
    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    // Mengembalikan nilai true jika jumlah kata kurang dari MAX_NO_OF_WORDS
    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }
}