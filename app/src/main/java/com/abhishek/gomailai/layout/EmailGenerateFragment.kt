package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.abhishek.gomailai.R
import com.abhishek.gomailai.databinding.FragmentEmailGenerateBinding


class EmailGenerateFragment : Fragment() {
    private lateinit var binding: FragmentEmailGenerateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEmailGenerateBinding.inflate(inflater, container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val emailGenerateViewModel = ViewModelProvider(this).get(EmailGenerateViewModel::class.java)

        /*// Handle button click to send prompt
        buttonAI.setOnClickListener {
            val prompt = editTextAI.text.toString()
            if (prompt.isNotEmpty()) {
                emailGenerateViewModel.sendPrompt(prompt) // Send only text prompt
            } else {
                Toast.makeText(this, "Please enter a prompt", Toast.LENGTH_SHORT).show()
            }
        }*/

        /*// Observe the ViewModel's uiState
        lifecycleScope.launch {
            emailGenerateViewModel.uiState.collect { uiState ->
                when (uiState) {
                    is UiState.Initial -> {
                        textView.text = "Waiting for input..."
                    }
                    is UiState.Loading -> {
                        textView.text = "Processing..."
                    }
                    is UiState.Success -> {
                        textView.text = uiState.outputText
                    }
                    is UiState.Error -> {
                        textView.text = "Error: ${uiState.errorMessage}"
                        Toast.makeText(this@MainActivity, uiState.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }*/

    }


}