package com.abhishek.gomailai.core.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import com.abhishek.gomailai.core.utils.DatabaseConst

@Entity(
    tableName = DatabaseConst.USER_TABLE,
    indices = [
        Index(value = [DatabaseConst.NAME], unique = true),
        Index(value = [DatabaseConst.EMAIL], unique = true)
    ]
)
data class UsersEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val password: String = "",
    val isLoggedIn: Boolean = false,
    val lastLoggedIn: Long = 0,
)

