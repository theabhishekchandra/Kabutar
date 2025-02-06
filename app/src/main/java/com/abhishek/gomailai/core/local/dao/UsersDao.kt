package com.abhishek.gomailai.core.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.utils.DatabaseConst
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert
    suspend fun insertUser(usersEntity: UsersEntity)

    @Update
    suspend fun updateUser(usersEntity: UsersEntity)

    @Delete
    suspend fun deleteUser(usersEntity: UsersEntity)

    @Query("SELECT * FROM  ${DatabaseConst.USER_TABLE}")
    fun getAllUsers(): Flow<List<UsersEntity>>

    @Query("UPDATE ${DatabaseConst.USER_TABLE} SET numberMails = :numberMails WHERE email = :email")
    fun updateNumberMailsByEmail(email: String, numberMails: Int)
}
