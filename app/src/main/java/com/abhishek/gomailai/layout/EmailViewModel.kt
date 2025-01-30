package com.abhishek.gomailai.layout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.mail.Session
import javax.mail.Message
import javax.mail.Transport
import javax.mail.PasswordAuthentication
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties

class EmailViewModel : ViewModel() {

    private val _emailSentStatus = MutableLiveData<Boolean>()
    val emailSentStatus: LiveData<Boolean> get() = _emailSentStatus

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

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