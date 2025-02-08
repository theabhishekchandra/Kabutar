package com.abhishek.gomailai.core.model

data class EmailWorkerDM(
    val senderEmail: String? = null,
    val recipientEmail: String? = null,
    val subject: String? = null,
    val messageBody: String? = null,
    val isEmailSend: Boolean = false,
    val stateName: String? = null,
    val tags: String? = null
)
