package com.abhishek.gomailai.core.workmanager

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.abhishek.gomailai.core.model.EmailWorkerDM
import com.abhishek.gomailai.core.utils.DatabaseConst.TAG
import com.abhishek.gomailai.core.utils.MainConst
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_IS_EMAIL_SEND
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_MESSAGE_BODY
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_RECIPIENT_EMAIL
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_SENDER_EMAIL
import com.abhishek.gomailai.core.utils.MainConst.WM_OUTPUT_DATA_SUBJECT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.activation.DataHandler
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.util.ByteArrayDataSource

class EmailSenderWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        private const val MAX_RETRIES = 3
        private const val INITIAL_BACKOFF_MS = 2000L
    }

    override suspend fun doWork(): Result {
        val senderName = inputData.getString(MainConst.WM_SENDER_NAME) ?: return Result.failure()
        val senderEmail = inputData.getString(MainConst.WM_SENDER_EMAIL) ?: return Result.failure()
        val senderPassword = inputData.getString(MainConst.WM_SENDER_PASSWORD) ?: return Result.failure()
        val recipientEmail = inputData.getString(MainConst.WM_RECIPIENT_EMAIL) ?: return Result.failure()
        val recipientName = inputData.getString(MainConst.WM_RECIPIENT_NAME) ?: return Result.failure()
        val subject = inputData.getString(MainConst.WM_SUBJECT) ?: return Result.failure()
        val messageBody = inputData.getString(MainConst.WM_MESSAGE_BODY) ?: return Result.failure()
        val pdfUri = inputData.getString(MainConst.WM_ATTACHMENT_URI)

        var lastException: Exception? = null

        // Retry with exponential backoff
        for (attempt in 0 until MAX_RETRIES) {
            try {
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
                    return Result.success(outputData)
                } else {
                    Log.e(TAG, "Email failed to send to ${emailWorker.recipientEmail}, attempt ${attempt + 1}/$MAX_RETRIES")
                    if (attempt == MAX_RETRIES - 1) {
                        return Result.failure(outputData)
                    }
                }
            } catch (e: Exception) {
                lastException = e
                Log.e(TAG, "Error sending email (attempt ${attempt + 1}/$MAX_RETRIES): ${e.message}")
                e.printStackTrace()

                if (attempt == MAX_RETRIES - 1) {
                    val errorData = workDataOf(
                        "error" to e.message,
                        WM_OUTPUT_DATA_RECIPIENT_EMAIL to recipientEmail,
                        WM_OUTPUT_DATA_IS_EMAIL_SEND to false
                    )
                    return Result.failure(errorData)
                }
            }

            // Exponential backoff: 2s, 4s, 8s
            val backoffTime = INITIAL_BACKOFF_MS * (1 shl attempt)
            Log.d(TAG, "Retrying in ${backoffTime}ms...")
            delay(backoffTime)
        }

        val errorData = workDataOf(
            "error" to (lastException?.message ?: "Unknown error"),
            WM_OUTPUT_DATA_RECIPIENT_EMAIL to recipientEmail,
            WM_OUTPUT_DATA_IS_EMAIL_SEND to false
        )
        return Result.failure(errorData)
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
                put("mail.smtp.connectiontimeout", "10000")
                put("mail.smtp.timeout", "10000")
                put("mail.smtp.writetimeout", "10000")
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

                    // Attach PDF with original file name
                    pdfUri?.takeIf { it.isNotEmpty() }?.let { uriString ->
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
                                Log.d(TAG, "PDF attachment added: $fileName")
                            }
                            inputStream?.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "Error attaching PDF file: ${e.message}")
                            // Continue sending email without attachment
                        }
                    }

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
                throw e // Re-throw to trigger retry
            }
        }
    }
}
