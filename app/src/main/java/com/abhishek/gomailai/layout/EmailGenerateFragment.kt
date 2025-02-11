package com.abhishek.gomailai.layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.databinding.FragmentEmailGenerateBinding
import com.abhishek.gomailai.layout.viewmodel.EmailGenerateViewModel
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EmailGenerateFragment : Fragment() {

    private lateinit var binding: FragmentEmailGenerateBinding
    private val emailGenerateViewModel: EmailGenerateViewModel by viewModels()
    private val emailViewModel: EmailViewModel by viewModels()

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
        binding.toolbarGenerateText.textView.text = ButtonTextFiled.GENERATED_EMAIL.buttonText
        val prompt = binding.editTextPrompt.text.toString().trim()
        updateButtonTexts(prompt.isEmpty())
    }
    private fun updateButtonTexts(isPromptEmpty: Boolean) {
        binding.buttonGenerateEmail.text = if (isPromptEmpty) "Generate Prompts" else "Generate Email"
        binding.buttonMarkAsReady.text = if (isPromptEmpty) "Next Prompts" else "Mark As Ready"
    }

    private fun listener() {
        binding.toolbarGenerateText.imageView.setOnClickListener {
            navigation.getNavController().popBackStack()
        }

        binding.textViewGeneratedEmail.doOnTextChanged { _, _, _, _ -> checkIfFieldsAreEmpty() }
        binding.editTextPrompt.doOnTextChanged { _, _, _, _ -> checkIfFieldsAreEmpty() }

        binding.buttonGenerateEmail.setOnClickListener {
            handleGenerateEmailButtonClick()
        }

        binding.buttonMarkAsReady.setOnClickListener {
            handleMarkAsReadyButtonClick()
        }
    }

    private fun handleGenerateEmailButtonClick() {
        val prompt = binding.editTextPrompt.text.toString().trim()
        if (prompt.isNotEmpty()) {
            emailGenerateViewModel.sendPrompt(prompt)
        } else {
            val profile =  appSharedPref.getUserInfo().designation ?: ""
            val randomPrompt = MainConst.getRandomPrompt(profile)
            binding.editTextPrompt.setText(randomPrompt)
        }
    }

    private fun handleMarkAsReadyButtonClick() {
        val generatedTemplate = binding.textViewGeneratedEmail.text.toString()
        val buttonText = binding.buttonMarkAsReady.text.toString()
        val buttonAction = ButtonTextFiled.fromString(buttonText)

        when (buttonAction) {
            ButtonTextFiled.GENERATED_EMAIL -> {
                if (generatedTemplate.isNotEmpty()) {
                    // TODO: Save this Template
                    Toast.makeText(context, "Email Template is ready for sending.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Generate an email template first", Toast.LENGTH_SHORT).show()
                }
            }
            ButtonTextFiled.GENERATE_PROMPT, ButtonTextFiled.NEXT_PROMPTS -> {
                val profile =  appSharedPref.getUserInfo().designation ?: ""
                val randomPrompt = MainConst.getRandomPrompt(profile)
                binding.editTextPrompt.setText(randomPrompt)
            }
            ButtonTextFiled.MARK_AS_READY -> {
                emailViewModel.insertEmailTemplate(generatedTemplate, requireContext())
                binding.editTextPrompt.setText("")
                binding.textViewGeneratedEmail.setText("")
            }
            else -> {
                // Handle other cases if needed
            }
        }
    }




    private fun observer() {
        emailGenerateViewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.loader.visibility = View.VISIBLE
                }
                is UiState.Success -> {
                    binding.textViewGeneratedEmail.setText( state.outputText)
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

    private fun checkIfFieldsAreEmpty() {
        val isPromptEmpty = binding.editTextPrompt.text.toString().trim().isEmpty()
        val isGeneratedEmailEmpty = binding.textViewGeneratedEmail.text.toString().trim().isEmpty()

        binding.buttonGenerateEmail.text = if (isPromptEmpty) "Generate Prompts" else "Generate Email"
        binding.buttonMarkAsReady.text = when {
            isGeneratedEmailEmpty -> "Next Prompts"
            else -> "Mark As Ready"
        }
    }
}
enum class ButtonTextFiled(val buttonText: String) {
    GENERATED_EMAIL("Generate Email"),
    MARK_AS_READY("Mark As Ready"),
    GENERATE_PROMPT("Generate Prompts"),
    NEXT_PROMPTS("Next Prompts");

    companion object {

        fun fromString(buttonText: String): ButtonTextFiled? {
            return entries.find { it.buttonText == buttonText }
        }
    }
}
