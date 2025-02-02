package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import kotlinx.coroutines.flow.Flow

interface IEmailRepository {
    suspend fun insertEmail(email: EmailDataEntity)
    fun getAllEmails(): Flow<List<EmailDataEntity>>
    suspend fun deleteEmail(email: EmailDataEntity)
}
