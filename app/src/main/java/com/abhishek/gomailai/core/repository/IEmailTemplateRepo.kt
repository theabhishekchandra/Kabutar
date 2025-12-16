package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity

interface IEmailTemplateRepo {
    suspend fun getEmailTemplates(): DBResponseModel<List<EmailTemplateEntity>>
    suspend fun getEmailTemplateById(id: Int): DBResponseModel<EmailTemplateEntity?>
    suspend fun insertEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit>
    suspend fun deleteEmailTemplateById(id: Int): DBResponseModel<Unit>
    suspend fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit>
}
