package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.dao.EmailTemplateDao
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import javax.inject.Inject

class EmailTemplateRepo @Inject constructor(
    private val emailTemplateDao: EmailTemplateDao
) : IEmailTemplateRepo {

    override fun getEmailTemplates(): List<EmailTemplateEntity> {
        return emailTemplateDao.getAllEmailTemplates()
    }

    override fun getEmailTemplateById(id: Int): EmailTemplateEntity? {
        return emailTemplateDao.getEmailTemplateById(id)
    }

    override fun insertEmailTemplate(emailTemplate: EmailTemplateEntity) {
        emailTemplateDao.insertEmailTemplate(emailTemplate)
    }

    override fun deleteEmailTemplateById(id: Int) {
        emailTemplateDao.deleteEmailTemplateById(id)
    }

    override fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity) {
        emailTemplateDao.deleteEmailTemplate(emailTemplate)
    }
}
