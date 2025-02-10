package com.abhishek.gomailai.core.workmanager

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.abhishek.gomailai.core.model.EmailWorkerDM
import com.abhishek.gomailai.core.repository.EmailRepositoryImpl
import com.abhishek.gomailai.core.utils.DatabaseConst.TAG
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_IS_EMAIL_SEND
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_MESSAGE_BODY
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_RECIPIENT_EMAIL
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_SENDER_EMAIL
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_SUBJECT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.activation.DataHandler
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

    override suspend fun doWork(): Result {
        val senderName = inputData.getString(MainConst.WM_SENDER_NAME) ?: return Result.failure()
        val senderEmail = inputData.getString(MainConst.WM_SENDER_EMAIL) ?: return Result.failure()
        val senderPassword =
            inputData.getString(MainConst.WM_SENDER_PASSWORD) ?: return Result.failure()
        val recipientEmail =
            inputData.getString(MainConst.WM_RECIPIENT_EMAIL) ?: return Result.failure()
        val recipientName =
            inputData.getString(MainConst.WM_RECIPIENT_NAME) ?: return Result.failure()
        val subject = inputData.getString(MainConst.WM_SUBJECT) ?: return Result.failure()
        val messageBody = inputData.getString(MainConst.WM_MESSAGE_BODY) ?: return Result.failure()
        val pdfUri = inputData.getString(MainConst.WM_ATTACHMENT_URI)

        return try {
            val emailWorker = sendEmail(
                senderName,
                senderEmail,
                senderPassword,
                recipientEmail,
                recipientName,
                subject,
                messageBody,
                pdfUri,
                applicationContext
            )

            val outputData = workDataOf(
                WM_OUTPUT_DATA_SENDER_EMAIL to emailWorker.senderEmail,
                WM_OUTPUT_DATA_RECIPIENT_EMAIL to emailWorker.recipientEmail,
                WM_OUTPUT_DATA_SUBJECT to emailWorker.subject,
                WM_OUTPUT_DATA_MESSAGE_BODY to emailWorker.messageBody,
                WM_OUTPUT_DATA_IS_EMAIL_SEND to emailWorker.isEmailSend
            )

            if (emailWorker.isEmailSend) {
                Log.d(TAG, "Email sent successfully to ${emailWorker.recipientEmail}")
                Result.success(outputData)
            } else {
                Log.e(TAG, "Email failed to send to ${emailWorker.recipientEmail}")
                Result.failure(outputData)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending email: ${e.message}")
            e.printStackTrace()
            val errorData = workDataOf("error" to e.message)
            Result.failure(errorData)
        }
    }
    private suspend fun sendEmail(
        senderName: String,
        senderEmail: String,
        senderPassword: String,
        recipientEmail: String,
        recipientName: String,
        subject: String,
        messageBody: String,
        pdfUri: String?,
        context: Context
    ): EmailWorkerDM {
        return withContext(Dispatchers.IO) {
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
                    setFrom(InternetAddress(senderEmail, senderName))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    setSubject(subject)

                    val multipart = MimeMultipart()

                    val textPart = MimeBodyPart()
                    textPart.setText(messageBody)
                    multipart.addBodyPart(textPart)

                    // Attach PDF properly with original file name
                    /*pdfUri?.takeIf { it.isNotEmpty() }?.let { uriString ->
                        try {
                            val uri = Uri.parse(uriString)
                            val contentResolver = context.contentResolver
                            val cursor = contentResolver.query(uri, null, null, null, null)
                            val fileName = cursor?.use {
                                if (it.moveToFirst()) {
                                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                                    if (nameIndex != -1) it.getString(nameIndex) else "Resume.pdf"
                                } else "Resume.pdf"
                            } ?: "Resume.pdf"

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
                            return@withContext EmailWorkerDM(
                                senderEmail = senderEmail,
                                recipientEmail = recipientEmail,
                                subject = subject,
                                messageBody = messageBody,
                                isEmailSend = false
                            )
                        }
                    }*/

                    setContent(multipart)
                }

                Transport.send(message)
                Log.d(TAG, "Email sent successfully to: $recipientEmail")
                return@withContext EmailWorkerDM(
                    senderEmail = senderEmail,
                    recipientEmail = recipientEmail,
                    subject = subject,
                    messageBody = messageBody,
                    isEmailSend = true
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error sending email: ${e.message}")
                e.printStackTrace()
                return@withContext EmailWorkerDM(
                    senderEmail = senderEmail,
                    recipientEmail = recipientEmail,
                    subject = subject,
                    messageBody = messageBody,
                    isEmailSend = false
                )
            }
        }
    }
}