package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.databinding.FragmentSendEmailBinding
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import com.abhishek.gomailai.layout.viewmodel.UserViewModel


class SendEmailFragment : Fragment() {
    private lateinit var binding: FragmentSendEmailBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendEmailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Use the ViewModel
    }

    private fun validate() : Boolean {
        val emailSubject = binding.emailSubjectTemplateEditText.text.toString()
        val emailBody = binding.emailBodyTemplateEditText.text.toString()
        if (emailSubject.isEmpty()) {
            binding.emailSubjectTemplateEditText.error = "Please enter a subject"
            return false
        }
        if (emailSubject.isNotEmpty()) {
            binding.emailSubjectTemplateEditText.error = "Please enter a Email Body"
            return false
        }
        return true
    }
}