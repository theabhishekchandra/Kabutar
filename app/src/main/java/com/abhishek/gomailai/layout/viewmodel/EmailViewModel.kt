package com.abhishek.gomailai.layout.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.model.EmailDM
import com.abhishek.gomailai.core.model.UserInfo
import com.abhishek.gomailai.core.repository.EmailRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.mail.Session
import javax.mail.Message
import javax.mail.Transport
import javax.mail.PasswordAuthentication
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailViewModel @Inject constructor(
    private val emailRepository: EmailRepositoryImpl,
) : ViewModel() {

    private val _emailSentStatus = MutableLiveData<Boolean>()
    val emailSentStatus: LiveData<Boolean> get() = _emailSentStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _saveUserInfo = MutableLiveData<UserInfo?>()
    val saveUserInfo: LiveData<UserInfo?> get() = _saveUserInfo

    private val _loadMailData = MutableLiveData<List<EmailDM>>()
    val loadMailData: LiveData<List<EmailDM>> get() = _loadMailData

    private val _loadMailDataAll = MutableLiveData<Set<EmailDM>>()
    private val loadMailDataAll: LiveData<Set<EmailDM>> get() = _loadMailDataAll

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _responseMessage = MutableLiveData<String?>()
    val responseMessage: LiveData<String?> = _responseMessage

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _responseMessage.value = "Exception handled: ${throwable.localizedMessage}"
    }

    init {
        if (!_loadMailDataAll.value.isNullOrEmpty()) {

        }
    }

    // Synchronize email data with local database (insert if not exists)
    private fun insertEmail(email: EmailDataEntity) = viewModelScope.launch(exceptionHandler) {
        emailRepository.insertEmail(email)
        Log.d("EmailViewModel", "Inserted email: ${email.email}")
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
    fun getAllEmails() {
        val emailList = MutableStateFlow<List<EmailDataEntity>>(emptyList())
        viewModelScope.launch {
            emailRepository.getAllEmails().collect {
                emailList.value = it
                Log.d("EmailViewModel", "Fetched emails from DB: ${it.size}")
            }
        }
    }

    fun deleteEmail(email: EmailDataEntity) {
        viewModelScope.launch {
            emailRepository.deleteEmail(email)
            Log.d("EmailViewModel", "Deleted email: ${email.email}")
        }
    }

    // Set extracted email data
    fun setMasterList(extractedData: List<EmailDM>) {
        _loadMailData.value = extractedData
        _loadMailDataAll.value = extractedData.toSet()
        uploadEmailInDatabase()
    }

    fun setUserInformation(userInfo: UserInfo) {
        _saveUserInfo.value = userInfo
    }

    /*fun sendEmail(
        senderEmail: String,
        senderPassword: String,
        recipientEmail: String,
        subject: String,
        messageBody: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Email properties
                val properties = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                // Authenticate
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                // Create the message
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    this.subject = subject
                    setText(messageBody)
                }

                // Send the email
                Transport.send(message)
                _emailSentStatus.postValue(true) // Email sent successfully
                Log.d("EmailViewModel", "Email sent successfully to: $recipientEmail")

            } catch (e: MessagingException) {
                _emailSentStatus.postValue(false)
                _errorMessage.postValue("Failed to send email: ${e.message}")
                Log.e("EmailViewModel", "Error sending email", e)
            }
        }
    }*/

    fun sendEmail(
        senderEmail: String,
        senderPassword: String,
        recipientEmail: String,
        subject: String,
        messageBody: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val properties = System.getProperties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(properties, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, senderPassword)
                }
            })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(senderEmail))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    setSubject(subject)
                    setText(messageBody)
                }

                Transport.send(message)
                println("Email sent successfully to: $recipientEmail")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}