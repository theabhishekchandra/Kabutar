package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.dao.EmailDataDao
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EmailRepositoryImpl @Inject constructor(
    private val emailDataDao: EmailDataDao
) : IEmailRepository {

    override suspend fun insertEmail(email: EmailDataEntity) {
        emailDataDao.insertEmail(email)
    }

    override fun getAllEmails(): Flow<List<EmailDataEntity>> {
        return emailDataDao.getAllEmails()
    }

    override suspend fun deleteEmail(email: EmailDataEntity) {
        emailDataDao.deleteEmail(email)
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
}
