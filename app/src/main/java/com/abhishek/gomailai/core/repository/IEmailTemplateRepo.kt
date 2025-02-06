package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity

interface IEmailTemplateRepo {
    fun getEmailTemplates(): DBResponseModel<List<EmailTemplateEntity>>
    fun getEmailTemplateById(id: Int): DBResponseModel<EmailTemplateEntity?>
    fun insertEmailTemplate(emailTemplate: EmailTemplateEntity) : DBResponseModel<Unit>
    fun deleteEmailTemplateById(id: Int) : DBResponseModel<Unit>
    fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit>
}
