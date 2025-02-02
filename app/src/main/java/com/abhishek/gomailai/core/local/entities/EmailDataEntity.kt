package com.abhishek.gomailai.core.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.abhishek.gomailai.core.utils.DatabaseConst

@Entity(tableName = DatabaseConst.EMAIL_DATA_TABLE,
    indices = [
        Index(value = [DatabaseConst.EMAIL], unique = true)
    ]
)
data class EmailDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name : String = "",
    val email: String = "",
    val title: String = "",
    val company: String = "",
    val isUsed: Boolean = false
)