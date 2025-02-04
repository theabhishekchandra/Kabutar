package com.abhishek.gomailai.core.model

open class SendEmailDM (
    val recipient: String = "",
    val subject: String = "",
    val body: String = "",
    val cc: List<String>? = null,
    val bcc: List<String>? = null,
    val attachments: String? = null,
)