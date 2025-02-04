package com.abhishek.gomailai.layout

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.BuildConfig
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentEmailGenerateBinding
import com.abhishek.gomailai.layout.viewmodel.EmailGenerateViewModel
import javax.inject.Inject

class EmailGenerateFragment : Fragment() {

    private lateinit var binding: FragmentEmailGenerateBinding
    private val emailGenerateViewModel: EmailGenerateViewModel by viewModels()

    @Inject
    lateinit var appSharedPref: IAPPSharedPref
    @Inject
    lateinit var navigation: INavigation

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

        initialize()
        listener()
        observer()
    }

    private fun initialize() {
        binding.toolbarGenerateText.textView.text = "Generate Email"
    }

    private fun observer() {
        emailGenerateViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loader.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.textViewGeneratedEmail.text = state.outputText
                    binding.loader.visibility = View.GONE
                }
                is UiState.Error -> {
                    Toast.makeText(context, "Error: ${state.errorMessage}", Toast.LENGTH_SHORT).show()
                    binding.loader.visibility = View.GONE
                }
                is UiState.Initial -> {
                    binding.loader.visibility = View.GONE
                }
            }
        }
    }

    private fun listener() {
        binding.toolbarGenerateText.imageView.setOnClickListener {
            navigation.getNavController().popBackStack()
        }
        binding.buttonGenerateEmail.setOnClickListener {
            val prompt = binding.editTextPrompt.text.toString().trim()
            if (prompt.isNotEmpty()) {
                emailGenerateViewModel.sendPrompt(prompt)
            } else {
                Toast.makeText(context, "Please enter a prompt to generate email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonMarkAsReady.setOnClickListener {
            val generatedTemplate = binding.textViewGeneratedEmail.text.toString().trim()
            if (generatedTemplate.isNotEmpty()) {
                // Mark the email as ready for bulk send
                Toast.makeText(context, "Email Template is ready for sending.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Generate an email template first", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
