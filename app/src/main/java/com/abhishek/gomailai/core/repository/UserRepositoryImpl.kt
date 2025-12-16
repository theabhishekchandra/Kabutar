package com.abhishek.gomailai.core.repository

import com.abhishek.gomailai.core.local.DBResponseModel
import com.abhishek.gomailai.core.local.dao.UsersDao
import com.abhishek.gomailai.core.local.entities.UsersEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersDao: UsersDao
) : IUserRepository {

    override suspend fun insertUser(user: UsersEntity): DBResponseModel<Unit> {
        return try {
            usersDao.insertUser(user)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error(e.message ?: "Unknown error occurred")
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

    override suspend fun updateNumberMailsByEmail(email: String, newMailCount: Int): DBResponseModel<Unit> {
        return try {
            usersDao.updateNumberMailsByEmail(email, newMailCount)
            DBResponseModel.Success(Unit)
        } catch (e: Exception) {
            DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }

    override suspend fun getTotalNumberMails(): Int {
        return usersDao.getTotalNumberMails() ?: 0
    }

    override suspend fun getUsersDetails(): DBResponseModel<UsersEntity> {
        return try {
            val user = usersDao.getUsersDetails()
            DBResponseModel.Success(user)
        } catch (e: Exception) {
            DBResponseModel.Error(e.message ?: "Unknown error occurred")
        }
    }
}
