package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.dao.EmailTemplateDao
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import javax.inject.Inject

class EmailTemplateRepo @Inject constructor(
    private val emailTemplateDao: EmailTemplateDao
) : IEmailTemplateRepo {

    override suspend fun getEmailTemplates(): DBResponseModel<List<EmailTemplateEntity>> {
        return try {
            val emailTemplates = emailTemplateDao.getAllEmailTemplates()
            DBResponseModel.Success(emailTemplates)
        } catch (e: Exception) {
            DBResponseModel.Error("Failed to get templates: ${e.message}", e)
        }
    }

    override suspend fun getEmailTemplateById(id: Int): DBResponseModel<EmailTemplateEntity?> {
        return try {
            val emailTemplate = emailTemplateDao.getEmailTemplateById(id)
            DBResponseModel.Success(emailTemplate)
        } catch (e: Exception) {
            DBResponseModel.Error("Failed to get template: ${e.message}", e)
        }
    }

    override suspend fun insertEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit> {
        return try {
            emailTemplateDao.insertEmailTemplate(emailTemplate)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteEmailTemplateById(id: Int): DBResponseModel<Unit> {
        return try {
            emailTemplateDao.deleteEmailTemplateById(id)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit> {
        return try {
            emailTemplateDao.deleteEmailTemplate(emailTemplate)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }
}
