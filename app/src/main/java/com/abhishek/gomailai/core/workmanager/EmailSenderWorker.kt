package com.abhishek.gomailai.core.workmanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abhishek.gomailai.core.utils.MainConst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailSenderWorker (context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val senderEmail = inputData.getString(MainConst.WM_SENDER_EMAIL) ?: return@withContext Result.failure()
                val senderPassword = inputData.getString(MainConst.WM_SENDER_PASSWORD) ?: return@withContext Result.failure()
                val recipientEmail = inputData.getString(MainConst.WM_RECIPIENT_EMAIL) ?: return@withContext Result.failure()
                val subject = inputData.getString(MainConst.WM_SUBJECT) ?: return@withContext Result.failure()
                val messageBody = inputData.getString(MainConst.WM_MESSAGE_BODY) ?: return@withContext Result.failure()

                sendEmail(senderEmail, senderPassword, recipientEmail, subject, messageBody)

                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }
    private fun sendEmail(
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