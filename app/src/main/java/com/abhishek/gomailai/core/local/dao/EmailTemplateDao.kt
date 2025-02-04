package com.abhishek.gomailai.core.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import com.abhishek.gomailai.core.utils.DatabaseConst

@Dao
interface EmailTemplateDao {
    @Insert
    fun insertEmailTemplate(emailTemplate: EmailTemplateEntity)

    @Query("SELECT * FROM ${DatabaseConst.EMAIL_TEMPLATE_TABLE} WHERE id = :id")
    fun getEmailTemplateById(id: Int): EmailTemplateEntity?

    @Query("SELECT * FROM ${DatabaseConst.EMAIL_TEMPLATE_TABLE}")
    fun getAllEmailTemplates(): List<EmailTemplateEntity>

    @Query("DELETE FROM ${DatabaseConst.EMAIL_TEMPLATE_TABLE} WHERE id = :id")
    fun deleteEmailTemplateById(id: Int)

    @Delete
    fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity)
}
