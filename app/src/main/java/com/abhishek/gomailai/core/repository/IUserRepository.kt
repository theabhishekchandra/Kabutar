package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.entities.UsersEntity
import kotlinx.coroutines.flow.Flow

interface IUserRepository {
    suspend fun insertUser(user: UsersEntity) : DBResponseModel<Unit>
    fun getAllUsers(): Flow<DBResponseModel<List<UsersEntity>>>
    suspend fun deleteUser(user: UsersEntity) : DBResponseModel<Unit>

}