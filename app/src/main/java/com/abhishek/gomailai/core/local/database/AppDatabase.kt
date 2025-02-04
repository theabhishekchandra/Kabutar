package com.abhishek.gomailai.core.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.abhishek.gomailai.core.local.dao.EmailDataDao
import com.abhishek.gomailai.core.local.dao.EmailTemplateDao
import com.abhishek.gomailai.core.local.dao.UsersDao
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.utils.DatabaseConst

@Database(entities = [UsersEntity::class, EmailDataEntity::class, EmailTemplateEntity::class], version = DatabaseConst.DB_VERSION, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UsersDao
    abstract fun emailDao(): EmailDataDao
    abstract fun emailTemplateDao(): EmailTemplateDao
}

