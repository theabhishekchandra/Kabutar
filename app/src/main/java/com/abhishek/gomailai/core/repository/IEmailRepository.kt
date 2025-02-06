package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import kotlinx.coroutines.flow.Flow

interface IEmailRepository {
    suspend fun insertEmail(email: EmailDataEntity): DBResponseModel<Unit>
    fun getAllEmails(): Flow<DBResponseModel<List<EmailDataEntity>>>
    suspend fun deleteEmail(email: EmailDataEntity): DBResponseModel<Unit>
    suspend fun markEmailAsUsed(email: String): DBResponseModel<Unit>
}
