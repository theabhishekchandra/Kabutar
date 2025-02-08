package com.abhishek.gomailai.layout

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.abhishek.gomailai.core.appsharepref.IAPPSharedPref
import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.nav.INavigation
import com.abhishek.gomailai.core.utils.DatabaseConst.TAG
import com.abhishek.gomailai.core.utils.MainConst.EMAIL_SENDING_WORKER_TAG
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_RECIPIENT_EMAIL
import com.abhishek.gomailai.core.workmanager.WorkManagerViewModel
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
    private val viewModel: WorkManagerViewModel by viewModels()

    @Inject
    lateinit var appSharedPref: IAPPSharedPref
    @Inject
    lateinit var navigation: INavigation

    private var selectedPdfUri: Uri? = null

    // File picker launcher
    private val pdfPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedPdfUri = uri
                Toast.makeText(requireContext(), "PDF Selected", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "Selected PDF URI: $uri")
            }
        }
    }

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
        viewModel.updateTaskStatuses(EMAIL_SENDING_WORKER_TAG)
    }



    private fun updateButtonState() {
        binding.buttonConfirmEmail.text = if (selectedPdfUri == null) "Select Resume" else "Send Mail"
    }

    override fun onResume() {
        super.onResume()
        emailViewModel.getAllEmails()
        updateButtonState()
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
            errorMessage.observe(viewLifecycleOwner){
                it.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
            isLoading.observe(viewLifecycleOwner){
                binding.loader.visibility = if (it) View.VISIBLE else View.GONE
            }
        }

        if (selectedPdfUri == null) binding.buttonConfirmEmail.text = "Select Resume" else binding.buttonConfirmEmail.text = "Send Mail"

        viewModel.taskEmailList.observe(viewLifecycleOwner){
            it.forEach { workData ->
                // Perform necessary actions based on the status of the work
                if (workData.isEmailSend){
                    emailViewModel.markEmailAsUsed(workData.recipientEmail.toString())
                    val value = appSharedPref.getUserInfo().numberMails?: 0
                    userViewModel.updateUserNumberMails(workData.senderEmail.toString(), value -1)
                    appSharedPref.setUserNumberMails(value-1)
                }
            }
        }
    }
    private fun checkIfFieldsAreEmpty(){

        val isEmailSubjectEmpty = binding.editTextSubject.text.toString().trim().isEmpty()
        val isEmailBodyEmpty = binding.editTextEmailBody.text.toString().trim().isEmpty()
    }

    private fun listener() {
        binding.toolbar.imageView.setOnClickListener {
            navigation.getNavController().popBackStack()
        }

        binding.buttonConfirmEmail.setOnClickListener {
            val value = appSharedPref.getUserInfo()
            emailViewModel.setUserInformation(value)

            if (selectedPdfUri == null) {
                openFilePicker()
            } else {
                if (validate()) {
                    val emailSubject = binding.editTextSubject.text.toString()
                    val emailBody = binding.editTextEmailBody.text.toString()
                    emailViewModel.sendBulkEmail(
                        emailSubject,
                        emailBody,
                        selectedPdfUri,
                        requireContext()
                    )
                    navigation.getNavController().popBackStack()
                }
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
            binding.editTextEmailBody.error = "Please enter an email body"
            return false
        }
//        if (value <= 0) {
//            Toast.makeText(requireContext(), "Please Buy Email Data, Your have $value mail", Toast.LENGTH_SHORT).show()
//            return false
//        }
        if (selectedPdfUri == null) {
            Toast.makeText(requireContext(), "Please select a Resume file", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        pdfPickerLauncher.launch(intent)
    }
}
