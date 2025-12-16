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
    suspend fun insertEmailTemplate(emailTemplate: EmailTemplateEntity)

    @Query("SELECT * FROM ${DatabaseConst.EMAIL_TEMPLATE_TABLE} WHERE id = :id")
    suspend fun getEmailTemplateById(id: Int): EmailTemplateEntity?

    @Query("SELECT * FROM ${DatabaseConst.EMAIL_TEMPLATE_TABLE}")
    suspend fun getAllEmailTemplates(): List<EmailTemplateEntity>

    @Query("DELETE FROM ${DatabaseConst.EMAIL_TEMPLATE_TABLE} WHERE id = :id")
    suspend fun deleteEmailTemplateById(id: Int)

    @Delete
    suspend fun deleteEmailTemplate(emailTemplate: EmailTemplateEntity)
}
