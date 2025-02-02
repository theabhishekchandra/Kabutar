package com.abhishek.gomailai.core.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abhishek.gomailai.core.utils.DatabaseConst

@Entity(tableName = DatabaseConst.EMAIL_TEMPLATE_TABLE)
data class EmailTemplateEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uID : String = "",
    val subject : String = "",
    val body : String = ""
)