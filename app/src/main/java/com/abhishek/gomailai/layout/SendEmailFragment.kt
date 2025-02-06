package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.model.EmailTemplateDM
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.databinding.FragmentSendEmailBinding
import com.abhishek.gomailai.layout.viewmodel.EmailViewModel
import com.abhishek.gomailai.layout.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SendEmailFragment : Fragment() {
    private lateinit var binding: FragmentSendEmailBinding
    private val emailViewModel: EmailViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    @Inject
    lateinit var appSharedPref: IAPPSharedPref
    @Inject
    lateinit var navigation: INavigation

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendEmailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailViewModel.getAllEmailTemplates()
        initialize()
        listener()
        observer()
    }

    override fun onResume() {
        super.onResume()
        emailViewModel.getAllEmails()
    }

    private fun observer() {
        with(emailViewModel) {
            emailTemplateLiveData.observe(viewLifecycleOwner) { listTemplate ->
                if (listTemplate.isNotEmpty()) {
                    binding.buttonNextTemplate.setOnClickListener {
                        binding.editTextSubject.setText(listTemplate.random().subject.toString())
                        binding.editTextEmailBody.setText(listTemplate.random().body.toString())
                    }
                }
            }
        }
    }

    private fun listener() {
        binding.toolbar.imageView.setOnClickListener {
            navigation.getNavController().popBackStack()
        }

        binding.buttonConfirmEmail.setOnClickListener {
            val value = appSharedPref.getUserInfo()
            emailViewModel.setUserInformation(value)
            if (validate()) {
                val emailSubject = binding.editTextSubject.text.toString()
                val emailBody = binding.editTextEmailBody.text.toString()
                emailViewModel.sendBulkEmail(emailSubject, emailBody, requireContext())
                navigation.getNavController().popBackStack()
            }
        }
    }

    private fun initialize() {
        binding.toolbar.textView.text = "Send Email"
    }

    private fun validate() : Boolean {
        val value = appSharedPref.getUserInfo().numberMails ?: 0
        val emailSubject = binding.editTextSubject.text.toString()
        val emailBody = binding.editTextEmailBody.text.toString()
        if (emailSubject.isEmpty()) {
            binding.editTextSubject.error = "Please enter a subject"
            return false
        }
        if (emailBody.isEmpty()) {
            binding.editTextEmailBody.error = "Please enter a Email Body"
            return false
        }
//        if (value <= 0) {
//            Toast.makeText(requireContext(), "Please Buy Email Data, Your have $value mail", Toast.LENGTH_SHORT).show()
//            return false
//        }
        return true
    }
}