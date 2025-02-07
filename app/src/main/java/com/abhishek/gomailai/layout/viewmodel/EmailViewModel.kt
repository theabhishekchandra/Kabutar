package com.abhishek.gomailai.layout.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkContinuation
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import com.abhishek.gomailai.core.model.EmailDM
import com.abhishek.gomailai.core.model.EmailTemplateDM
import com.abhishek.gomailai.core.model.UserInfo
import com.abhishek.gomailai.core.repository.EmailRepositoryImpl
import com.abhishek.gomailai.core.repository.EmailTemplateRepo
import com.abhishek.gomailai.core.utils.DatabaseConst.TAG
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.core.workmanager.EmailSenderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val emailRepository: EmailRepositoryImpl,
    private val templateRepository: EmailTemplateRepo
) : ViewModel() {

    private val _emailSentStatus = MutableLiveData<Boolean>()
    val emailSentStatus: LiveData<Boolean> get() = _emailSentStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _saveUserInfo = MutableLiveData<UserInfo?>()
    val saveUserInfo: LiveData<UserInfo?> get() = _saveUserInfo

    private val _loadMailData = MutableLiveData<List<EmailDataEntity>>()
    val loadMailData: LiveData<List<EmailDataEntity>> get() = _loadMailData

    private val _emailTemplateLiveData = MutableLiveData<List<EmailTemplateDM>>()
    val emailTemplateLiveData: LiveData<List<EmailTemplateDM>> = _emailTemplateLiveData

    private val _loadMailDataAll = MutableLiveData<Set<EmailDM>>()
    private val loadMailDataAll: LiveData<Set<EmailDM>> get() = _loadMailDataAll

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<String?>()
    val responseMessage: LiveData<String?> = _responseMessage

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _responseMessage.value = "Exception handled: ${throwable.localizedMessage}"
    }

    fun insertEmailTemplate(email: String,context: Context) {
        val emailData = extractSubjectAndBody(email,context)
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            val emailTemplate = EmailTemplateEntity(
                subject = emailData?.first?:"",
                body = emailData?.second?:"",
            )
            try {
                val response = templateRepository.insertEmailTemplate(emailTemplate)
                when(response){
                    is DBResponseModel.Success -> _responseMessage.value = response.message
                    is DBResponseModel.Error -> _responseMessage.value = response.message
                    else -> {}
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Fetch all email templates
    fun getAllEmailTemplates() {
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            try {
                val templates = templateRepository.getEmailTemplates()
                templates.let {
                    when (templates) {
                        is DBResponseModel.Success -> _emailTemplateLiveData.value = templates.data.map { entity ->
                            EmailTemplateDM(
                                id = entity.id.toString(),
                                uID = entity.uID,
                                subject = entity.subject,
                                body = entity.body
                            )
                        }
                        is DBResponseModel.Error -> _responseMessage.value = templates.message
                        else -> {}
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Delete email template
    fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity) {
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true
            try {
                templateRepository.deleteEmailTemplate(emailTemplate)
                Log.d(TAG, "Deleted email template: ${emailTemplate.body}")
            } finally {
                _isLoading.value = false
            }
        }
    }
    // Synchronize email data with local database (insert if not exists)
    fun insertEmail(email: EmailDataEntity) = viewModelScope.launch(exceptionHandler) {
        emailRepository.insertEmail(email)
        Log.d(TAG, "Inserted email: ${email.email}")
    }

    // Upload emails from external source (i.e. extracted from somewhere like API or UI) to database
    private fun uploadEmailInDatabase() {
        viewModelScope.launch {
            val emailList = _loadMailDataAll.value
            emailList?.forEach { emailDM ->
                val emailEntity = EmailDataEntity(
                    email = emailDM.email ?: "",
                    name = emailDM.name ?: "",
                    title = emailDM.title ?: "",
                    company = emailDM.company ?: ""
                )
                // Ensure email is inserted only if it doesn't already exist
                insertEmail(emailEntity)
            }
        }
    }
    // Fetch all emails Company from local database
    fun getAllEmails() {
        viewModelScope.launch {
            emailRepository.getAllEmails().collect { result ->
                result.let {
                    when (result) {
                        is DBResponseModel.Success -> _loadMailData.value = result.data
                        is DBResponseModel.Error -> _responseMessage.value = result.message
                        else -> {}
                    }
                }
            }
        }
    }

    fun deleteEmail(email: EmailDataEntity) {
        viewModelScope.launch {
            emailRepository.deleteEmail(email)
            Log.d(TAG, "Deleted email: ${email.email}")
        }
    }

    // Set extracted email data
    fun setMasterList(extractedData: List<EmailDM>) {
        _loadMailDataAll.value = extractedData.toSet()
        uploadEmailInDatabase()
    }

    fun setUserInformation(userInfo: UserInfo) {
        _saveUserInfo.value = userInfo
    }
    @SuppressLint("EnqueueWork")
    fun sendBulkEmail(
        emailSubject: String,
        emailBody: String,
        pdfUri: Uri?,
        context: Context
    ) {
        val email = _saveUserInfo.value?.email ?: return
        val password = _saveUserInfo.value?.password ?: return
        val numberMails = _saveUserInfo.value?.numberMails ?: return
        val companyMails = _loadMailData.value ?: return  // Get all emails
        val validEmails = companyMails.filter { !it.isUsed }  // Filter unused emails

        if (validEmails.isEmpty()) {
            Toast.makeText(context, "No available email to send", Toast.LENGTH_LONG).show()
            return
        }

        if (/*numberMails*/ 1 > 0) {
            val workRequests = mutableListOf<OneTimeWorkRequest>()

            for (i in 0 until /*numberMails*/2) {
//                val selectedEmail = validEmails.getOrNull(i) ?: break // Pick the next available email

                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val inputData = workDataOf(
                    MainConst.WM_SENDER_EMAIL to email,
                    MainConst.WM_SENDER_PASSWORD to password,
                    MainConst.WM_RECIPIENT_EMAIL to "ac928920@gmail.com", // TODO: Change this to recipient email.
                    MainConst.WM_SUBJECT to emailSubject,
                    MainConst.WM_MESSAGE_BODY to emailBody,
                    MainConst.WM_ATTACHMENT_URI to (pdfUri?.toString() ?: "")
                )

                val emailWorkRequest = OneTimeWorkRequestBuilder<EmailSenderWorker>()
                    .setInitialDelay(i * 5L, TimeUnit.SECONDS)
                    .addTag(MainConst.EMAIL_SENDING_WORKER_TAG)
                    .setInputData(inputData)
                    .setConstraints(constraints)
                    .build()

                workRequests.add(emailWorkRequest)
//                WorkManager.getInstance(context).enqueue(emailWorkRequest)

                // Mark the email as used in the database
                /*GlobalScope.launch {
                    emailRepository.markEmailAsUsed(selectedEmail.email)
                }*/
            }

            // Begin work chain
            WorkManager.getInstance(context)
                .beginWith(workRequests)
                .enqueue()

            Toast.makeText(context, "Emails are being sent", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "No emails to send", Toast.LENGTH_LONG).show()
        }
    }


    private fun extractSubjectAndBody(emailContent: String, context: Context): Pair<String, String>? {
        val missingFields = mutableListOf<String>()

        // Split email content into subject and body
        val lines = emailContent.lines()

        if (lines.isEmpty()) {
            Toast.makeText(context, "Invalid email format", Toast.LENGTH_LONG).show()
            return null
        }

        val subject = lines.first().trim()
        val body = lines.drop(1).joinToString("\n").trim()

        val placeholderRegex = "\\[([^\\]]+)\\]".toRegex()

        val missingSubjectFields = placeholderRegex.findAll(subject).map { it.value }.toList()
        val missingBodyFields = placeholderRegex.findAll(body).map { it.value }.toList()

        missingFields.addAll(missingSubjectFields)
        missingFields.addAll(missingBodyFields)

        return if (missingFields.isEmpty()) {
            Pair(subject, body)
        } else {
                Toast.makeText(
                    context,
                    "Please fill in the following fields: ${missingFields.joinToString(", ")}",
                    Toast.LENGTH_LONG
                ).show()
            null
        }
    }
}