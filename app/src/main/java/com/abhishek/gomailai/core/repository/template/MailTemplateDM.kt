package com.abhishek.gomailai.core.repository.template

data class MailTemplateDM(
    val id: String = "",
    val title: String = "",
    val industry: String = "",
    val body: String = "",
    val tags: List<String> = emptyList(),
)
