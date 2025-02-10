package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.dao.EmailDataDao
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailDataDao: EmailDataDao
) : IEmailRepository {

    override suspend fun insertEmail(email: EmailDataEntity): DBResponseModel<Unit> {
        return try {
            emailDataDao.insertEmail(email)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error("Insert failed: ${e.message}", e)
        }
    }

    override fun getAllEmails(): Flow<DBResponseModel<List<EmailDataEntity>>> = flow {
        try {
            emailDataDao.getAllEmails().collect { allMail ->
                emit(DBResponseModel.Success(allMail))
            }
        } catch (e: Exception) {
            emit(DBResponseModel.Error(e.localizedMessage ?: "Unknown Error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteEmail(email: EmailDataEntity): DBResponseModel<Unit> {
        return try {
            emailDataDao.deleteEmail(email)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error("Delete failed: ${e.message}", e)
        }
    }

    // Synchronizing email data (similar to user data)
    suspend fun synchronizeEmails() {
        // Fetch all email entries (from external source, e.g., API or local db sync)
        val localEmails = emailDataDao.getAllEmails().first()  // Get all emails from DB
        // Example: Fetching emails from API or other data source, replace with actual source
        val apiEmails = listOf<EmailDataEntity>()  // Assume fetched data from API

        // Synchronizing local and external sources
        apiEmails.forEach { apiEmail ->
            // Insert new or updated emails from API (if not already in DB)
            emailDataDao.insertIfNotExists(apiEmail)
        }

        // Remove any emails that are no longer needed (logic can be customized)
        localEmails.forEach { localEmail ->
            if (!apiEmails.contains(localEmail)) {
                emailDataDao.deleteEmail(localEmail)
            }
        }
    }

    override suspend fun markEmailAsUsed(email: String): DBResponseModel<Unit> {
        return try {
            emailDataDao.updateEmailUsageStatus(email, true)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error("Error: ${e.message}", e)
        }
    }

}
