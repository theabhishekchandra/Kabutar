package com.abhishek.gomailai.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.abhishek.gomailai.core.local.database.AppDatabase
import com.abhishek.gomailai.core.local.entities.EmailDataEntity
import com.abhishek.gomailai.core.local.entities.EmailTemplateEntity
import com.abhishek.gomailai.core.local.entities.UsersEntity
import com.abhishek.gomailai.core.utils.DatabaseConst
import com.abhishek.gomailai.core.utils.DatabaseConst.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providedDatabase(@ApplicationContext context: Context)
    = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DatabaseConst.DB_NAME)
        .allowMainThreadQueries()
        .addMigrations(MIGRATION_1_2)
        /*.fallbackToDestructiveMigration() // This will reset the database*/
        .build()

    // DAO
    @Provides
    @Singleton
    fun provideEmailDao(database: AppDatabase) = database.emailDao()

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideEmailTemplateDao(database: AppDatabase) = database.emailTemplateDao()

    // Entity
    @Provides
    @Singleton
    fun provideEmailEntity() = EmailDataEntity()

    @Provides
    @Singleton
    fun provideUserEmail() = UsersEntity()

    @Provides
    @Singleton
    fun provideEmailTemplateEntity() = EmailTemplateEntity()

}