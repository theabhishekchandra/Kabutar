package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.dao.UsersDao
import com.abhishek.gomailai.core.local.entities.UsersEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersDao: UsersDao
) : IUserRepository {

    override suspend fun insertUser(user: UsersEntity): DBResponseModel<Unit> {
        try {
            usersDao.insertUser(user)
            return DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            return DBResponseModel.Error(e.message ?: "Unknown error occurred")

        }
    }

    override fun getAllUsers(): Flow<DBResponseModel<List<UsersEntity>>> = flow {
        try {
            usersDao.getAllUsers().collect { users ->
                emit(DBResponseModel.Success(users))
            }
        } catch (e: Exception) {
            emit(DBResponseModel.Error(e.localizedMessage ?: "Unknown Error"))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteUser(user: UsersEntity): DBResponseModel<Unit> {
        return try {
            usersDao.deleteUser(user)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error("Delete failed: ${e.message}", e)
        }
    }

    override fun updateNumberMailsByEmail(email: String, newMailCount: Int) : DBResponseModel<Unit>   {
        try {
            usersDao.updateNumberMailsByEmail(email, newMailCount)
            return DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            return DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }
}
