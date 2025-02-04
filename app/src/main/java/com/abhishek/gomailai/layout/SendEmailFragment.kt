package com.abhishek.gomailai.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
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
        // TODO: Use the ViewModel

        initialize()
        listener()
//        observer()
    }

    private fun observer() {
        TODO("Not yet implemented")
    }

    private fun listener() {
        binding.toolbar.imageView.setOnClickListener {
            navigation.getNavController().popBackStack()
        }
        binding.nextTemplateButton.setOnClickListener {

        }
        binding.confirmEmailButton.setOnClickListener {
            val value = appSharedPref.getUserInfo()
            emailViewModel.setUserInformation(value)
            if (validate()) {
                val emailSubject = binding.emailSubjectTemplateEditText.text.toString()
                val emailBody = binding.emailBodyTemplateEditText.text.toString()
                emailViewModel.sendBulkEmail(emailSubject, emailBody, requireContext())
            }
        }
    }

    private fun initialize() {
        binding.toolbar.textView.text = "Send Email"
    }

    private fun validate() : Boolean {
        val value = appSharedPref.getUserInfo().numberMails ?: 0
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
//        if (value <= 0) {
//            binding.emailBodyTemplateEditText.error = "Please Buy Email Data"
//            return false
//        }
        return true
    }
}