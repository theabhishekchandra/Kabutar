package com.abhishek.gomailai.core.workmanager

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abhishek.gomailai.core.repository.EmailRepositoryImpl
import com.abhishek.gomailai.core.utils.DatabaseConst.TAG
import com.abhishek.gomailai.core.utils.MainConst
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Properties
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.inject.Inject
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class EmailSenderWorker (context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @Inject
    lateinit var emailRepository: EmailRepositoryImpl


    override suspend fun doWork(): Result {
        val senderEmail = inputData.getString(MainConst.WM_SENDER_EMAIL) ?: return Result.failure()
        val senderPassword = inputData.getString(MainConst.WM_SENDER_PASSWORD) ?: return Result.failure()
        val recipientEmail = inputData.getString(MainConst.WM_RECIPIENT_EMAIL) ?: return Result.failure()
        val subject = inputData.getString(MainConst.WM_SUBJECT) ?: return Result.failure()
        val messageBody = inputData.getString(MainConst.WM_MESSAGE_BODY) ?: return Result.failure()
        val pdfUri = inputData.getString(MainConst.WM_ATTACHMENT_URI)

        return withContext(Dispatchers.IO) {
            try {
                sendEmail(senderEmail, senderPassword, recipientEmail, subject, messageBody, pdfUri, applicationContext)

//                emailRepository.markEmailAsUsed(recipientEmail)

                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Error sending email: ${e.message}")
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
        messageBody: String,
        pdfUri: String?,
        context: Context
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

                    val multipart = MimeMultipart()

                    val textPart = MimeBodyPart()
                    textPart.setText(messageBody)
                    multipart.addBodyPart(textPart)

                    // Attach PDF properly with original file name
                    pdfUri?.takeIf { it.isNotEmpty() }?.let { uriString ->
                        try {
                            val uri = Uri.parse(uriString)
                            val contentResolver = context.contentResolver
                            val cursor = contentResolver.query(uri, null, null, null, null)
                            val fileName = cursor?.use {
                                if (it.moveToFirst()) {
                                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    if (nameIndex != -1) it.getString(nameIndex) else "resume.pdf"
                                } else "resume.pdf"
                            } ?: "resume.pdf"

                            val inputStream = contentResolver.openInputStream(uri)
                            val byteArray = inputStream?.readBytes()

                            if (byteArray != null) {
                                val filePart = MimeBodyPart()
                                val dataSource = ByteArrayDataSource(byteArray, "application/pdf")
                                filePart.dataHandler = DataHandler(dataSource)
                                filePart.fileName = fileName
                                multipart.addBodyPart(filePart)
                            }
                            inputStream?.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error attaching file: ${e.message}")
                        }
                    }

                    setContent(multipart)
                }

                Transport.send(message)
                println("Email sent successfully to: $recipientEmail")
            } catch (e: Exception) {
                Log.e(TAG, "Error sending email: ${e.message}")
                e.printStackTrace()
            }
        }
    }

}