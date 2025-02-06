package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.dao.EmailTemplateDao
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import javax.inject.Inject

class EmailTemplateRepo @Inject constructor(
    private val emailTemplateDao: EmailTemplateDao
) : IEmailTemplateRepo {

    override fun getEmailTemplates(): DBResponseModel<List<EmailTemplateEntity>> {
        return try {
            val emailTemplates = emailTemplateDao.getAllEmailTemplates()
            DBResponseModel.Success(emailTemplates)
        } catch (e: Exception) {
            DBResponseModel.Error("Delete failed: ${e.message}", e)
        }

    }

    override fun getEmailTemplateById(id: Int): DBResponseModel<EmailTemplateEntity?> {
        return try {
            val emailTemplate = emailTemplateDao.getEmailTemplateById(id)
            DBResponseModel.Success(emailTemplate)
        } catch (e: Exception) {
            DBResponseModel.Error("Delete failed: ${e.message}", e)
        }
    }

    override fun insertEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit> {
        try {
            val response = emailTemplateDao.insertEmailTemplate(emailTemplate)
            return DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            return DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }

    override fun deleteEmailTemplateById(id: Int): DBResponseModel<Unit> {
        try {
            emailTemplateDao.deleteEmailTemplateById(id)
            return DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            return DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }

    override fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity): DBResponseModel<Unit> {
        try {
            emailTemplateDao.deleteEmailTemplate(emailTemplate)
            return DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            return DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }
}
