/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Bagian Fragmen yang berisi logika game dan tempat game dimainkan.
 */
class GameFragment : Fragment() {

    // Menginisialisasi GameViewModel
    private val viewModel: GameViewModel by viewModels()

    // Mengikat instance objek dengan akses ke tampilan di tata letak game_fragment.xml
    private lateinit var binding: GameFragmentBinding

    // Untuk meng-inflate XML tata letak yang menggunakan objek binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Penggunaan objek binding
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        binding.lifecycleOwner = viewLifecycleOwner

        // Untuk menyiapkan pemroses klik tombol KIRIM dan LEWATI.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
    }

    // Sebagai pemroses klik untuk tombol kirim dan menampilkan kata acak berikutnya.
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()

        if (viewModel.isUserWordCorrect(playerWord)) {
            setErrorTextField(false)
            if (!viewModel.nextWord()) {
                showFinalScoreDialog() // Untuk menampilkan dialog pemberitahuan setelah kata terakhir.
            }
        } else {
            setErrorTextField(true)
        }
    }

    // Sebagai pemroses klik untuk tombol lewati dengan tanpa mengubah skor yang ada.
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            showFinalScoreDialog()
        }
    }

    // Fungsi untuk membuat dan menampilkan dialog pemberitahuan.
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.congratulations)) // Untuk menampilkan judul pada dialog.
            .setMessage(getString(R.string.you_scored, viewModel.score.value)) // Menampilkan pesan skor akhir.
            .setCancelable(false) // Agar terus menampilkan dialog sampai tombol kembali ditekan.

            // Untuk membuat tombol teks KELUAR dan MAIN LAGI
            .setNegativeButton(getString(R.string.exit)) { _, _ -> exitGame() } // Tombol untuk keluar game.
            .setPositiveButton(getString(R.string.play_again)) { _, _ -> restartGame() } // Tombol untuk main lagi game.
            .show() // Bagian untuk menampilkan dialog pemberitahuan
    }

    // Untuk memulai ulang game.
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    // Untuk mengakhiri atau keluar dari game.
    private fun exitGame() {
        activity?.finish()
    }

    // Untuk mengatur dan memperbarui error dikolom teks.
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true // Memunculkan error
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false // Memperbarui kolom teks
            binding.textInputEditText.text = null
        }
    }
}
