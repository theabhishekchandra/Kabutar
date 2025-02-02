package com.abhishek.gomailai.core.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.utils.DatabaseConst
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDataDao {

    @Query("SELECT * FROM ${DatabaseConst.EMAIL_DATA_TABLE} WHERE email = :email LIMIT 1")
    suspend fun getEmail(email: String): EmailDataEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)  // Prevents overwriting existing data
    suspend fun insertEmail(email: EmailDataEntity): Long

    suspend fun insertIfNotExists(email: EmailDataEntity) {
        val existingEmail = getEmail(email.email)  // Check if email exists
        if (existingEmail == null) {
            insertEmail(email)  // Insert only if not present
        }
    }

    @Query("SELECT * FROM ${DatabaseConst.EMAIL_DATA_TABLE}")
    fun getAllEmails(): Flow<List<EmailDataEntity>>

    @Delete
    suspend fun deleteEmail(email: EmailDataEntity)
}