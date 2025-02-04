package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity

interface IEmailTemplateRepo {
    fun getEmailTemplates(): List<EmailTemplateEntity>
    fun getEmailTemplateById(id: Int): EmailTemplateEntity?
    fun insertEmailTemplate(emailTemplate: EmailTemplateEntity)
    fun deleteEmailTemplateById(id: Int)
    fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity)
}
